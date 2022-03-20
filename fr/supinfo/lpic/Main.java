package fr.supinfo.lpic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static String WAIT_FOLDER = null;
    public static String WORK_FOLDER = null;

    public static String workFile = null;
    public static String workName = null;
    public static int workLang = 0;

    public static int gradeId = 0;
    public static int courseId = 0;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("[!] Invalid arguments !");
            System.exit(0);
        }

        WAIT_FOLDER = args[0];
        WORK_FOLDER = args[1];

        ExercisesData.onInit();

        if (MysqlManager.onInit()) {
            System.out.println("[OK] Successful connection to the database !");
            searchNewFile();
        } else {
            System.err.println("[!] An error has occurred : <MYSQL DB>");
        }

    }

    public static void searchNewFile() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        System.out.println("\nWaiting for new file to be corrected ...");

        Runnable searchRunnable = new Runnable() {
            public void run() {
                String[] pathNames;

                File file = new File(WAIT_FOLDER);
                pathNames = file.list();

                if (pathNames.length > 0) {
                    executor.shutdown();

                    workFile = pathNames[0];
                    workName = workFile.substring(0, workFile.indexOf("."));

                    System.out.println("New file found : " + workFile);
                    moveFileInWorkFolder();
                }
            }
        };
        executor.scheduleAtFixedRate(searchRunnable, 0, 5, TimeUnit.SECONDS);
    }

    public static void moveFileInWorkFolder() {
        try {
            Files.move(Paths.get(WAIT_FOLDER + "/" + workFile), Paths.get(WORK_FOLDER + "/" + workFile));

            System.out.println("Copy the file to its execution environment");

            checkIfFileIsValid();
        } catch (IOException e) {
            System.out.println("Error when copying the file to its execution environment !");
            e.printStackTrace();
        }
    }

    public static void checkIfFileIsValid() {
        ArrayList<Integer> getExercise = MysqlManager.getExerciseInfo(workName);

        System.out.println("Checking the validity of the file ...");

        if (getExercise != null) {
            gradeId = getExercise.get(0);
            courseId = getExercise.get(1);
            workLang = getExercise.get(2);

            System.out.println("Valid file !");
            analyzeFileOutput();
        } else {
            System.out.println("Invalid file !");
            removeFile();
        }
    }

    public static void analyzeFileOutput() {
        HashMap<String[], String> exercise = ExercisesData.exercises.get(courseId);
        int goodAnswer = 0;
        int totalAnswer = exercise.size();

        System.out.println("Launching the file execution sequences ...");

        for (Map.Entry<String[], String> pass : exercise.entrySet()) {
            goodAnswer += ExecScript.onExec(pass.getKey(), pass.getValue());
        }

        System.out.println("End of execution sequences !");
        setUserGrade(gradeId, Math.round((goodAnswer * 20) / totalAnswer));
    }

    public static void setUserGrade(int id, int grade) {
        MysqlManager.setExerciseGrade(id, grade);

        System.out.println("New grade assigned to the user : " + grade + "/20");

        removeFile();
    }

    public static void removeFile() {
        File exercise = new File(WORK_FOLDER + "/" + workFile);

        System.out.println("Deleting the exercise file and resetting program ...");

        exercise.delete();

        workFile = null;
        workName = null;
        workLang = 0;
        gradeId = 0;
        courseId = 0;

        System.out.println("End of the treatment !");

        searchNewFile();
    }

}
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
            searchNewFile();
        } else {
            System.err.println("[!] An error has occurred : <MYSQL DB>");
        }

    }

    public static void searchNewFile() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable searchRunnable = new Runnable() {
            public void run() {
                String[] pathNames;

                File file = new File(WAIT_FOLDER);
                pathNames = file.list();

                if (pathNames.length > 0) {
                    executor.shutdown();

                    workFile = pathNames[0];
                    workName = workFile.substring(0, workFile.indexOf("."));

                    moveFileInWorkFolder();
                }
            }
        };
        executor.scheduleAtFixedRate(searchRunnable, 0, 5, TimeUnit.SECONDS);
    }

    public static void moveFileInWorkFolder() {
        try {
            Files.move(Paths.get(WAIT_FOLDER + "/" + workFile), Paths.get(WORK_FOLDER + "/" + workFile));

            checkIfFileIsValid();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkIfFileIsValid() {
        ArrayList<Integer> getExercise = MysqlManager.getExerciseInfo(workName);
        if (getExercise != null) {
            gradeId = getExercise.get(0);
            courseId = getExercise.get(1);
            workLang = getExercise.get(2);

            analyzeFileOutput();
        } else {
            removeFile();
        }
    }

    public static void analyzeFileOutput() {
        HashMap<String[], String> exercise = ExercisesData.exercises.get(courseId);
        int goodAnswer = 0;
        int totalAnswer = exercise.size();

        for (Map.Entry<String[], String> pass : exercise.entrySet()) {
            goodAnswer += ExecScript.onExec(pass.getKey(), pass.getValue());
        }

        setUserGrade(gradeId, Math.round((goodAnswer * 20) / totalAnswer));
    }

    public static void setUserGrade(int id, int grade) {
        MysqlManager.setExerciseGrade(id, grade);

        removeFile();
    }

    public static void removeFile() {
        File exercise = new File(WORK_FOLDER + "/" + workFile);
        exercise.delete();

        workFile = null;
        workName = null;
        workLang = 0;
        gradeId = 0;
        courseId = 0;

        searchNewFile();
    }

}
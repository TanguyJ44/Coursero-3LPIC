package fr.supinfo.lpic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecScript {

    public static int onExec(String[] args, String res) {
        String[] commands;

        if (Main.workLang == 1) {
            commands = new String[args.length + 2];

            commands[0] = "wine";

            commands[1] = Main.WORK_FOLDER + "/" + Main.workFile;

            for (int i = 0; i < args.length; i++) {
                commands[i+1] = String.valueOf(args[i]);
            }
        } else {
            commands = new String[args.length + 2];

            commands[0] = "python";

            commands[1] = Main.WORK_FOLDER + "/" + Main.workFile;

            for (int i = 0; i < args.length; i++) {
                commands[i+2] = String.valueOf(args[i]);
            }
        }

        int gradeResult = 0;

        Runtime rt = Runtime.getRuntime();
        Process proc = null;

        try {
            proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            String output = null;

            while ((output = stdInput.readLine()) != null) {
                System.out.println("Return : " + output);
                System.out.println("Expected  : " + res);

                if (output.equals(res)) {
                    System.out.println("Result : OK");
                    gradeResult = 1;
                } else {
                    System.out.println("Result : KO");
                    gradeResult = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            gradeResult = 0;
        } finally {
            return gradeResult;
        }
    }

}

package fr.supinfo.lpic;

import java.util.HashMap;

public class ExercisesData {

    public static HashMap<Integer, HashMap<String[], String>> exercises = new HashMap<>();

    public static HashMap<String[], String> ex2 = new HashMap<>();

    public static void onInit() {

        ex2.put(new String[]{}, "Hello World !");

        exercises.put(2, ex2);

    }

}

package fr.supinfo.lpic;

import java.util.HashMap;

public class ExercisesData {

    // Hello World Python
    public static HashMap<String[], String> ex1 = new HashMap<>();

    // Hello World C
    public static HashMap<String[], String> ex2 = new HashMap<>();

    // Addition Python
    public static HashMap<String[], String> ex3 = new HashMap<>();

    // Addition C
    public static HashMap<String[], String> ex4 = new HashMap<>();

    public static HashMap<Integer, HashMap<String[], String>> exercises = new HashMap<>();

    public static void onInit() {

        ex1.put(new String[]{}, "Hello, World!");

        ex2.put(new String[]{}, "Hello, World!");

        ex3.put(new String[]{"2", "4"}, "6");
        ex3.put(new String[]{"11", "5"}, "16");
        ex3.put(new String[]{"24", "10"}, "34");
        ex3.put(new String[]{"61", "6"}, "67");

        ex4.put(new String[]{"2", "4"}, "6");
        ex4.put(new String[]{"11", "5"}, "16");
        ex4.put(new String[]{"24", "10"}, "34");
        ex4.put(new String[]{"61", "6"}, "67");

        exercises.put(1, ex1);
        exercises.put(2, ex2);
        exercises.put(3, ex3);
        exercises.put(4, ex4);

    }

}

package fr.supinfo.lpic;

import java.sql.*;
import java.util.ArrayList;

public class MysqlManager {

    static Connection connection = null;

    static Statement statement = null;

    static ResultSet resultSet = null;

    public static boolean onInit() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lpic?autoReconnect=true", "root", "");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lpic?autoReconnect=true", "user", "root");

            statement = connection.createStatement();

            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Integer> getExerciseInfo(String fileId) {
        ArrayList<Integer> result = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM notes  INNER JOIN cours ON notes.Id_cours = cours.id WHERE notes.Id_fichier = " + fileId);

            if (resultSet.next()) {
                result.add(resultSet.getInt("notes.id"));
                result.add(resultSet.getInt("notes.Id_cours"));
                if (resultSet.getString("cours.langage").equals("C")) {
                    result.add(1);
                } else {
                    result.add(2);
                }
                return result;
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static void setExerciseGrade(int id, int grade) {
        try {
            if (!connection.isClosed() && !statement.isClosed()) {
                statement.execute("UPDATE notes SET note = " + grade + " WHERE Id = " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

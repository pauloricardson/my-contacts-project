package br.capacita.contatos.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/agenda";
    private static final String USER = "paulo";
    private static final String PASSWORD = "6032";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o banco de dados");
            e.printStackTrace();
            return null;
        }
    }
}
package br.capacita.contatos.application;

import br.capacita.contatos.database.DataBaseConnection;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(AgendaApplication.class, args);

        DataBaseConnection.connect();
    }
}

package br.capacita.contatos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AgendaApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Contatos");
        stage.show();
    }
}

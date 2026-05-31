package br.capacita.contatos.application;

import br.capacita.contatos.controller.MainViewController;
import br.capacita.contatos.util.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class AgendaApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        Preferences prefs = Preferences.userNodeForPackage(MainViewController.class);
        boolean darkMode = prefs.getBoolean("darkMode", true);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        scene.getStylesheets().add(
                getClass().getResource(
                        ThemeManager.getCurrentTheme()
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Contatos");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/contacts.png")));
        stage.setResizable(false);
        stage.show();
    }
}

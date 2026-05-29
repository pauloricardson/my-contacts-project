package br.capacita.contatos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private Button addContact;

    @FXML
    public void addContact(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddContactView.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Adicionar Contato");
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editContact(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditContactView.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Editar Contato");
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

package br.capacita.contatos.controller;

import br.capacita.contatos.models.Contact;
import br.capacita.contatos.service.ContactService;
import br.capacita.contatos.service.ContactServiceSingleton;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private final ContactService<Contact> contactService = ContactServiceSingleton.contactService;

    @FXML private TableView<Contact> contactsTable;
    @FXML private TableColumn<Contact, String> columnName;
    @FXML private TableColumn<Contact, String> columnPhone;
    @FXML private TableColumn<Contact, String> columnEmail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        columnEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        loadDataTable();
    }

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

    public void loadDataTable() {
        contactsTable.setItems(javafx.collections.FXCollections.observableArrayList(
                ContactServiceSingleton.contactService.listContacts()
        ));
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
}

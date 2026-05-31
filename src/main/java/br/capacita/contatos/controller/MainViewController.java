package br.capacita.contatos.controller;

import br.capacita.contatos.models.CommercialContact;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.service.ContactService;
import br.capacita.contatos.service.ContactServiceSingleton;
import br.capacita.contatos.util.Alerts;
import br.capacita.contatos.util.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private final ContactService<Contact> contactService = ContactServiceSingleton.contactService;

    @FXML private BorderPane root;
    @FXML private TableView<Contact> contactsTable;
    @FXML private TableColumn<Contact, String> columnName;
    @FXML private TableColumn<Contact, String> columnPhone;
    @FXML private TableColumn<Contact, String> columnEmail;
    @FXML private Label labelName;
    @FXML private Label labelPhone;
    @FXML private Label labelEmail;
    @FXML private Label labelAddress;
    @FXML private Label labelOrganization;
    @FXML private Label labelDateCreation;

    @FXML public void setTheme() {
        Scene scene = root.getScene();

        String darkCss =
                getClass().getResource("/styles/dark.css").toExternalForm();

        boolean isDark = scene.getStylesheets().contains(darkCss);

        ThemeManager.setDarkMode(!isDark);

        scene.getStylesheets().setAll(
                getClass().getResource(
                        ThemeManager.getCurrentTheme()
                ).toExternalForm()
        );
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        columnEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        contactsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        contactsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillPanel(newValue);
            } else  {
                clearPanel();
            }
        });

        loadDataTable();
    }

    @FXML public void addContact(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddContactView.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Adicionar Contato");
            Scene scene = new Scene(parent);

            scene.getStylesheets().add(
                    getClass().getResource(
                            ThemeManager.getCurrentTheme()
                    ).toExternalForm()
            );

            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());
            stage.showAndWait();
            loadDataTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void deleteContact(ActionEvent event) {
        Contact contactSelected = contactsTable.getSelectionModel().getSelectedItem();
        if (contactSelected == null) {
            Alerts.showAlerts("Aviso", null, "Selecione um contato na tabela primeiro!", Alert.AlertType.WARNING);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja apagar o contato \"" + contactSelected.getName() + "\"?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contactService.deleteContact(contactSelected.getId());

                Alerts.showAlerts("Sucesso", null, "Contato excluído com sucesso!", Alert.AlertType.INFORMATION);
                clearPanel();
                loadDataTable();
            } catch (Exception e) {
                Alerts.showAlerts("Erro", null, "Não foi possível excluir o contato.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML public void loadDataTable() {
        contactsTable.setItems(javafx.collections.FXCollections.observableArrayList(
                ContactServiceSingleton.contactService.listContacts()
        ));
    }

    @FXML public void cleanPanel(ActionEvent event) {
        clearPanel();
    }

    public void clearPanel() {
        contactsTable.getSelectionModel().clearSelection();
        labelName.setText("");
        labelPhone.setText("");
        labelEmail.setText("");
        labelAddress.setText("");
        labelOrganization.setText("");
        labelDateCreation.setText("");
    }

    public void fillPanel(Contact contact) {
        labelName.setText(contact.getName());
        labelPhone.setText(contact.getPhone());
        labelEmail.setText(contact.getEmail());
        labelAddress.setText(contact.getAddress());

        if (contact instanceof CommercialContact) {
            String organization = ((CommercialContact) contact).getOrganization();
            labelOrganization.setText(organization);
        } else {
            labelOrganization.setText("");
        }

        labelDateCreation.setText(contact.getCreatedAt().substring(0, 10));
        LocalDateTime dataHora = LocalDateTime.parse(
                contact.getCreatedAt(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        labelDateCreation.setText(
                dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    @FXML public void editContact(ActionEvent event) {
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

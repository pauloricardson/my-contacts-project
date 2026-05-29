package br.capacita.contatos.controller;

import br.capacita.contatos.models.CommercialContact;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.service.ContactService;
import br.capacita.contatos.service.ContactServiceSingleton;
import br.capacita.contatos.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class AddContactViewController implements Initializable {

    private final ContactService<Contact> contactService = ContactServiceSingleton.contactService;

    @FXML private TextField textName;
    @FXML private TextField textPhone;
    @FXML private TextField textEmail;
    @FXML private TextField textAddress;
    @FXML private TextField textOrganization;
    @FXML private CheckBox checkOrganization;
    @FXML private VBox vboxOrganization;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        vboxOrganization.disableProperty().bind(checkOrganization.selectedProperty().not());
    }

    @FXML public void addContact(ActionEvent event) {
        try {
            String name = textName.getText();
            String phone = textPhone.getText();
            String email = textEmail.getText();
            String address = textAddress.getText();

            Contact newContact;

            if (checkOrganization.isSelected()) {
                String organization = textOrganization.getText();
                newContact = new CommercialContact(name, phone, email, address, organization);
            } else {
                newContact = new Contact(name, phone, email, address);
            }

            contactService.addContact(newContact);

            Alerts.showAlerts("Sucesso", null, "Contato salvo com sucesso!", Alert.AlertType.INFORMATION);

            Button btn = (Button) event.getSource();
            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {
            Alerts.showAlerts("Erro de Validação", null, (e.getMessage()), Alert.AlertType.ERROR);
        } catch (Exception e) {
            Alerts.showAlerts("Erro", null, "Ocorreu um erro inesperado", Alert.AlertType.ERROR);
        }
    }

    @FXML public void close(ActionEvent event) {
        Button buttonClose = (Button) event.getSource();
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }
}

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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditContactViewController implements Initializable {

    private final ContactService<Contact> contactService = ContactServiceSingleton.contactService;
    private long idContactActual;
    @FXML private TextField textName;
    @FXML private TextField textPhone;
    @FXML private TextField textEmail;
    @FXML private TextField textAddress;
    @FXML private TextField textOrganization;
    @FXML private CheckBox checkOrganization;
    @FXML private VBox vboxOrganization;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vboxOrganization.disableProperty().bind(checkOrganization.selectedProperty().not());
    }

    public void selectContactForEditing(Contact contact) {
        this.idContactActual = contact.getId();
        this.textName.setText(contact.getName());
        this.textPhone.setText(contact.getPhone());
        this.textEmail.setText(contact.getEmail());
        this.textAddress.setText(contact.getAddress());

        if (contact instanceof CommercialContact) {
            this.checkOrganization.setSelected(true);
            this.textOrganization.setText(((CommercialContact) contact).getOrganization());
        } else {
            this.checkOrganization.setSelected(false);
            this.textOrganization.setText("");
        }
    }

    @FXML public void save(ActionEvent event) {
        try {
            String name = textName.getText();
            String phone = textPhone.getText();
            String email = textEmail.getText();
            String address = textAddress.getText();

            Contact contactUpadate;

            if (checkOrganization.isSelected()) {
                String organization = textOrganization.getText();
                contactUpadate = new CommercialContact(idContactActual, name, phone, email, address, organization, null);
            } else {
                contactUpadate = new Contact(idContactActual, name, phone, email, address, null);
            }

            contactService.updateContact(contactUpadate);

            Alerts.showAlerts("Sucesso", null, "Contato atualizado com sucesso!", Alert.AlertType.INFORMATION);

            Button btn = (Button) event.getSource();
            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {
            Alerts.showAlerts("Erro de Validação", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            Alerts.showAlerts("Erro", null, "Ocorreu um erro inesperado ao atualizar.", Alert.AlertType.ERROR);
        }
    }

    @FXML public void close(ActionEvent event) {
        Button buttonClose = (Button) event.getSource();
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }
}

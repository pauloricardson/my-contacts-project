package br.capacita.contatos.models;

import br.capacita.contatos.util.EmailValidator;
import br.capacita.contatos.util.PhoneValidator;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Contact {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty dateCriation = new SimpleStringProperty();

    public Contact(String name, String phone, String email, String address) {
        setName(name);
        setPhone(phone);
        setEmail(email);
        setAddress(address);
    }

    public Contact(long id, String name, String phone, String email, String address, String dateCriation) {
        this.id.set(id);
        setName(name);
        setPhone(phone);
        setEmail(email);
        setAddress(address);
        this.dateCriation.set(dateCriation);
    }

    public StringProperty nameProperty() {return name;}
    public StringProperty phoneProperty() {return phone;}
    public StringProperty emailProperty() {return email;}

    public long getId() {return id.get();}
    public String getName() {return name.get();}
    public String getPhone() {return phone.get();}
    public String getEmail() {return email.get();}
    public String getAddress() {return address.get();}
    public String getDateCriation() {return dateCriation.get();}

    public void setName(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        this.name.set(nome);
    }

    public void setPhone(String phone) {
        if (!PhoneValidator.phoneValidator(phone)) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        this.phone.set(phone);
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            this.email.set("");
            return;
        }
        if (!EmailValidator.emailValidator(email)) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        this.email.set(email);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
}

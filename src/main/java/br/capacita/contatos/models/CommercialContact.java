package br.capacita.contatos.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CommercialContact extends Contact{
    private final StringProperty organization = new SimpleStringProperty();

    public CommercialContact(String name, String phone, String email, String address, String organization) {
        super(name, phone, email, address);
        setOrganization(organization);
    }

    public CommercialContact(long id, String name, String phone, String email, String address, String organization, String createdAt) {
        super(id, name, phone, email, address, createdAt);
        setOrganization(organization);
    }

    public String getOrganization() {return organization.get();}

    public StringProperty organizationProperty() {return organization;}

    public void setOrganization(String organization) {
        if (organization == null || organization.isBlank()) {
            throw new IllegalArgumentException("Nome da organização inválida");
        }
        this.organization.set(organization);
    }
}

package br.capacita.contatos.repository;

import br.capacita.contatos.database.DataBaseConnection;
import br.capacita.contatos.models.CommercialContact;
import br.capacita.contatos.models.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ContactRepository {

    public void save(Contact contact) {
        String sql = "INSERT INTO contacts (name, phone, email, address, organization) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getPhone());

            if (contact.getEmail().trim().isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, contact.getEmail());
            }

            if (contact.getAddress().trim().isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, contact.getAddress());
            }

            if (contact instanceof CommercialContact) {
                CommercialContact commercialContact = (CommercialContact) contact;
                stmt.setString(5, commercialContact.getOrganization());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            stmt.executeUpdate();
            System.out.println("Contato salvo no banco de dados com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Não foi possível salvar o contato.");
        }
    }

}

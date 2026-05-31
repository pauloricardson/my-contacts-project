package br.capacita.contatos.repository;

import br.capacita.contatos.database.DataBaseConnection;
import br.capacita.contatos.models.CommercialContact;
import br.capacita.contatos.models.Contact;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ContactRepository<T extends Contact> {

    public void save(T contact) {
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

    public ObservableList<Contact> searchContacts() {
        ObservableList<Contact> list = FXCollections.observableArrayList();

        String sql = "SELECT id, name, phone, email, address, organization, created_at FROM contacts";

        try (Connection conn = DataBaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String organization = rs.getString("organization");
                String createdAt = rs.getString("created_at");

                if (organization != null && !organization.isBlank()) {
                    list.add(new CommercialContact(id, name, phone, email, address, organization, createdAt));
                } else {
                    list.add(new Contact(id, name, phone, email, address, createdAt));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar contatos no banco: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM contacts WHERE id = ?";

        try (Connection conn = DataBaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar contato no MySQL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Não foi possível excluir o contato do banco de dados.");
        }
    }

    public void updateContact(T contact) {
        String sql = "UPDATE contacts SET name = ?, phone = ?, email = ?, address = ?, organization = ? WHERE id = ?";

        try (Connection conn = DataBaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getPhone());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, contact.getAddress());

            if (contact instanceof CommercialContact) {
                stmt.setString(5, ((CommercialContact) contact).getOrganization());
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            stmt.setLong(6, contact.getId());
            stmt.executeUpdate();
            System.out.println("Contato atualizado no MySQL com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar o contato no banco.");
        }
    }
}

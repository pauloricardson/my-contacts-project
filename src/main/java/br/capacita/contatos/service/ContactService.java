package br.capacita.contatos.service;

import br.capacita.contatos.exeptions.ContactNotFindException;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.DAO.contactDAO;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ContactService<T extends Contact> {
    private final contactDAO<T> contactDAO = new contactDAO<>();

    public void addContact(T contact) {
        contactDAO.save(contact);
    }

    public List<Contact> listContacts() {
        return contactDAO.searchContacts();
    }

    public List<Contact> listContacts(String filter) {
        if (filter == null || filter.isBlank()) {
            return listContacts();
        }

        String term = normalize(filter.toLowerCase());
        List<Contact> result = new ArrayList<>();

        for (Contact contact : listContacts()) {
            String nameContact = normalize(contact.getName().toLowerCase());
            if (nameContact.contains(term)) {
                result.add(contact);
            }
        }
        return result;
    }

    public boolean deleteContact(long id) throws ContactNotFindException {
        boolean deleted = contactDAO.delete(id);

        if (!deleted) {
            throw new ContactNotFindException("Nenhum contato encontrado");
        }
        return true;
    }

    public void updateContact(T contact) {
        contactDAO.updateContact(contact);
    }

    public String normalize(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        return text.replaceAll("[^\\p{ASCII}]", "");
    }

}

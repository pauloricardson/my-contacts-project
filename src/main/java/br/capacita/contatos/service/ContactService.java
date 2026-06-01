package br.capacita.contatos.service;

import br.capacita.contatos.exeptions.ContactNotFindException;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.repository.ContactRepository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ContactService<T extends Contact> {
    private final ContactRepository<T> contactRepository = new ContactRepository<>();

    public void addContact(T contact) {
        contactRepository.save(contact);
    }

    public List<Contact> listContacts() {
        return contactRepository.searchContacts();
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
        boolean deleted = contactRepository.delete(id);

        if (!deleted) {
            throw new ContactNotFindException("Nenhum contato encontrado");
        }
        return true;
    }

    public void updateContact(T contact) {
        contactRepository.updateContact(contact);
    }

    public String normalize(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        return text.replaceAll("[^\\p{ASCII}]", "");
    }

}

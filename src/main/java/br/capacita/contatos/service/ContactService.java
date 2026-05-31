package br.capacita.contatos.service;

import br.capacita.contatos.exeptions.ContactNotFindException;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.repository.ContactRepository;

import java.util.List;

public class ContactService<T extends Contact> {
    private final ContactRepository<T> contactRepository = new ContactRepository<>();

    public void addContact(T contact) {
        contactRepository.save(contact);
    }

    public List<Contact> listContacts() {
        return contactRepository.searchContacts();
    }

    public boolean deleteContact(long id) throws ContactNotFindException {
        boolean deleted = contactRepository.delete(id);

        if (!deleted) {
            throw new ContactNotFindException("Nenhum contato encontrado");
        }
        return true;
    }

}

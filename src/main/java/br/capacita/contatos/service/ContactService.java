package br.capacita.contatos.service;

import br.capacita.contatos.models.Contact;
import br.capacita.contatos.repository.ContactRepository;

public class ContactService {
    private final ContactRepository contactRepository = new ContactRepository();

    public void addContact(Contact contact) {
        contactRepository.save(contact);
    }

}

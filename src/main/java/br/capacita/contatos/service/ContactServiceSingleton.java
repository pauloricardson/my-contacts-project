package br.capacita.contatos.service;

import br.capacita.contatos.models.Contact;

public class ContactServiceSingleton {
    public static ContactService<Contact> contactService = new ContactService<>();
}

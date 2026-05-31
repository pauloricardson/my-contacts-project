package br.capacita.contatos.exeptions;

public class ContactNotFindException extends RuntimeException {
    public ContactNotFindException(String message) {
        super(message);
    }
}

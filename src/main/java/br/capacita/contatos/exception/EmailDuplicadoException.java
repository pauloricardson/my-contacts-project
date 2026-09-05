package br.capacita.contatos.exception;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String mensagem) {
        super(mensagem);
    }

    public static EmailDuplicadoException contato(String email) {
        return new EmailDuplicadoException("Já existe um contato com o e-mail '" + email + "' cadastrado para este usuário.");
    }

    public static EmailDuplicadoException usuario(String email) {
        return new EmailDuplicadoException("O e-mail '" + email + "' já está cadastrado.");
    }
}

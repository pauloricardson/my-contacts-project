package br.capacita.contatos.exception;

public class ContatoNaoEncontradoException extends RuntimeException {

    public ContatoNaoEncontradoException(Long id) {
        super("Contato não encontrado (id=" + id + ") para o usuário autenticado.");
    }
}

package br.capacita.contatos.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tratamento global de erros da API: converte exceções em respostas
 * padronizadas (RFC 7807 - ProblemDetail) com os status corretos.
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContatoNaoEncontradoException.class)
    public ProblemDetail contatoNaoEncontrado(ContatoNaoEncontradoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Contato não encontrado");
        return pd;
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ProblemDetail emailDuplicado(EmailDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflito de dados");
        return pd;
    }

    /**
     * Violação de integridade do banco (ex.: e-mail duplicado para o mesmo usuário
     * pela unique constraint uk_contatos_usuario_email). Retorna 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridadeDeDados(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Violação de integridade dos dados (ex.: e-mail já cadastrado para este usuário).");
        pd.setTitle("Conflito de dados");
        return pd;
    }

    /** Validação de DTO (@NotBlank, @Email, @Pattern...) reprovada. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacaoInvalida(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos estão inválidos.");
        pd.setTitle("Requisição inválida");
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        pd.setProperty("campos", campos);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpoInvalido(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado.");
        pd.setTitle("Requisição inválida");
        return pd;
    }

    /** Credenciais incorretas no login. */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail credenciaisInvalidas(AuthenticationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        pd.setTitle("Não autenticado");
        return pd;
    }

    /** Reprovado pelo @PreAuthorize (usuário não é o dono do recurso). */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail acessoNegado(AuthorizationDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "Acesso negado: você não é o dono deste contato.");
        pd.setTitle("Acesso negado");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> erroInesperado(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado. Tente novamente mais tarde.");
        pd.setTitle("Erro interno");
        return ResponseEntity.internalServerError().body(pd);
    }
}

package br.capacita.contatos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação (login).")
public record LoginRequestDTO(

        @Schema(description = "E-mail cadastrado", example = "paulo@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Senha do usuário", example = "senha123")
        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}

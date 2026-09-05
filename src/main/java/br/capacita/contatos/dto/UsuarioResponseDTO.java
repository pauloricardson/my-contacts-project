package br.capacita.contatos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de um usuário cadastrado (sem a senha).")
public record UsuarioResponseDTO(

        @Schema(description = "Identificador do usuário", example = "1")
        Long id,

        @Schema(description = "Nome do usuário", example = "Paulo Ricardson")
        String nome,

        @Schema(description = "E-mail do usuário", example = "paulo@email.com")
        String email
) {
}

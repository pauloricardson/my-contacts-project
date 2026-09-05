package br.capacita.contatos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT emitido após o login.")
public record TokenResponseDTO(

        @Schema(description = "Token JWT para usar no cabeçalho Authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tipo,

        @Schema(description = "Validade do token em milissegundos", example = "86400000")
        Long expiraEmMs
) {
}

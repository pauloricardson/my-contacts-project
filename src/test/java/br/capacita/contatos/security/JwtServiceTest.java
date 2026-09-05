package br.capacita.contatos.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "segredo-super-secreto-de-teste-com-mais-de-32-caracteres";
    private static final long EXPIRACAO_MS = 60_000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRACAO_MS);
    }

    @Test
    void deveGerarTokenComEmailNoSubject() {
        String token = jwtService.gerarToken("paulo@email.com");
        assertTrue(jwtService.tokenValido(token));
        assertEquals("paulo@email.com", jwtService.extrairEmail(token));
    }

    @Test
    void deveRejeitarTokenAdulterado() {
        String token = jwtService.gerarToken("paulo@email.com");
        String adulterado = token.substring(0, token.length() - 2) + "xx";
        assertFalse(jwtService.tokenValido(adulterado));
    }

    @Test
    void deveRejeitarTokenDeOutraChave() {
        JwtService outroServico = new JwtService("outra-chave-totalmente-diferente-e-bem-longa-123456", EXPIRACAO_MS);
        String tokenDeOutraChave = outroServico.gerarToken("paulo@email.com");
        assertFalse(jwtService.tokenValido(tokenDeOutraChave));
    }

    @Test
    void deveRejeitarTokenExpirado() {
        JwtService servicoExpirado = new JwtService(SECRET, -1000L);
        String tokenExpirado = servicoExpirado.gerarToken("paulo@email.com");
        assertFalse(jwtService.tokenValido(tokenExpirado));
    }

    @Test
    void deveRejeitarTokenNuloOuVazio() {
        assertFalse(jwtService.tokenValido(null));
        assertFalse(jwtService.tokenValido(""));
        assertFalse(jwtService.tokenValido("nao-e-um-jwt"));
    }
}

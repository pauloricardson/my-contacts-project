package br.capacita.contatos.service;

import br.capacita.contatos.dto.LoginRequestDTO;
import br.capacita.contatos.dto.TokenResponseDTO;
import br.capacita.contatos.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Autentica as credenciais e, se válidas, emite um token JWT
     * (erros de credencial são traduzidos para 401 pelo GlobalExceptionHandler).
     */
    public TokenResponseDTO login(LoginRequestDTO request) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        String token = jwtService.gerarToken(authentication.getName());
        return new TokenResponseDTO(token, "Bearer", jwtService.getExpirationMs());
    }
}

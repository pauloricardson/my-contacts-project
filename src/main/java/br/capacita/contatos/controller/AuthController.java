package br.capacita.contatos.controller;

import br.capacita.contatos.dto.LoginRequestDTO;
import br.capacita.contatos.dto.RegistroRequestDTO;
import br.capacita.contatos.dto.TokenResponseDTO;
import br.capacita.contatos.dto.UsuarioResponseDTO;
import br.capacita.contatos.service.AuthService;
import br.capacita.contatos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Registro de novos usuários e login para emissão do token JWT.")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastrar um novo usuário", description = "Cria a conta de um usuário. A senha é armazenada com hash BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (nome, e-mail ou senha fora do formato)"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        UsuarioResponseDTO usuario = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar e obter o token JWT", description = "Valida as credenciais e retorna o token para usar nos endpoints protegidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso (token retornado)"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

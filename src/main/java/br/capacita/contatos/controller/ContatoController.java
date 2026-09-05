package br.capacita.contatos.controller;

import br.capacita.contatos.dto.ContatoRequestDTO;
import br.capacita.contatos.dto.ContatoResponseDTO;
import br.capacita.contatos.security.UsuarioDetalhes;
import br.capacita.contatos.service.ContatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contatos")
@Tag(name = "Contatos", description = "CRUD da agenda de contatos privada do usuário autenticado.")
@SecurityRequirement(name = "bearerAuth")
public class ContatoController {

    private final ContatoService contatoService;

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @GetMapping
    @Operation(summary = "Listar contatos", description = "Retorna todos os contatos do usuário autenticado; use 'nome' para filtrar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)")
    })
    public ResponseEntity<List<ContatoResponseDTO>> listar(
            @Parameter(description = "Filtro opcional por nome (contém, ignorando maiúsculas)")
            @RequestParam(required = false) String nome,
            @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        return ResponseEntity.ok(contatoService.listar(detalhes.getUsuarioId(), nome));
    }

    @GetMapping("/busca")
    @Operation(summary = "Buscar contatos por termo (JPQL)", description = "Busca por termo em nome, e-mail, organização ou telefone (consulta customizada).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'termo' não informado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)")
    })
    public ResponseEntity<List<ContatoResponseDTO>> buscarPorTermo(
            @Parameter(description = "Termo buscado em nome, e-mail, organização ou telefone", required = true)
            @RequestParam String termo,
            @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        return ResponseEntity.ok(contatoService.buscarPorTermo(detalhes.getUsuarioId(), termo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar um contato", description = "Retorna os dados de um contato do usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado (ou não pertence ao usuário)")
    })
    public ResponseEntity<ContatoResponseDTO> detalhar(@PathVariable Long id,
                                                       @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        return ResponseEntity.ok(contatoService.buscarPorId(id, detalhes.getUsuarioId()));
    }

    @PostMapping
    @Operation(summary = "Criar um contato", description = "Cria um novo contato vinculado ao usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contato criado (Location com a URI do recurso)"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (@NotBlank, @Email, @Pattern do telefone)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado para este usuário")
    })
    public ResponseEntity<ContatoResponseDTO> criar(@Valid @RequestBody ContatoRequestDTO request,
                                                    @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        ContatoResponseDTO criado = contatoService.criar(request, detalhes.getUsuarioId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getId())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@contatoService.pertenceAoUsuario(#id, authentication.name)")
    @Operation(summary = "Atualizar um contato", description = "Atualiza totalmente um contato. Autorização em nível de método: apenas o dono do contato pode atualizá-lo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não é o dono do contato"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado para este usuário")
    })
    public ResponseEntity<ContatoResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ContatoRequestDTO request,
                                                        @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        return ResponseEntity.ok(contatoService.atualizar(id, request, detalhes.getUsuarioId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@contatoService.pertenceAoUsuario(#id, authentication.name)")
    @Operation(summary = "Excluir um contato", description = "Remove um contato. Autorização em nível de método: apenas o dono do contato pode excluí-lo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contato excluído (sem corpo de resposta)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não é o dono do contato"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @AuthenticationPrincipal UsuarioDetalhes detalhes) {
        contatoService.deletar(id, detalhes.getUsuarioId());
        return ResponseEntity.noContent().build();
    }
}

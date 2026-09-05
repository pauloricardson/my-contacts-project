package br.capacita.contatos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Representação de um contato retornada pela API (GET).")
public class ContatoResponseDTO {

    @Schema(description = "Identificador do contato", example = "1")
    private Long id;

    @Schema(description = "Nome do contato", example = "Paulo Ricardson")
    private String nome;

    @Schema(description = "Telefone do contato", example = "(85) 98170-8058")
    private String telefone;

    @Schema(description = "E-mail do contato", example = "paulo@email.com")
    private String email;

    @Schema(description = "Endereço do contato", example = "Rua 1, Fortaleza-CE")
    private String endereco;

    @Schema(description = "Organização (contatos comerciais)", example = "IFCE")
    private String organizacao;

    @Schema(description = "Data/hora de criação do contato")
    private LocalDateTime criadoEm;

    @Schema(description = "Id do usuário dono do contato", example = "1")
    private Long usuarioId;

    public ContatoResponseDTO() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }

    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getOrganizacao() { return organizacao; }

    public void setOrganizacao(String organizacao) { this.organizacao = organizacao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Long getUsuarioId() { return usuarioId; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}

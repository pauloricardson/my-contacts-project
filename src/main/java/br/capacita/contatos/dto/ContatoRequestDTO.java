package br.capacita.contatos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar/atualizar um contato (POST/PUT).")
public class ContatoRequestDTO {

    public static final String REGEX_TELEFONE_BR = "^\\(?\\d{2}\\)?\\s?(9?\\d{4})-?\\d{4}$";

    @Schema(description = "Nome do contato", example = "Paulo Ricardson")
    @NotBlank(message = "O nome do contato é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @Schema(description = "Telefone no padrão brasileiro", example = "(85) 98170-8058")
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = REGEX_TELEFONE_BR, message = "Telefone inválido! Exemplos válidos: (85) 98170-8058, 88981708058")
    @Size(max = 20)
    private String telefone;

    @Schema(description = "E-mail do contato (opcional, único por usuário)", example = "paulo@email.com")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
    private String email;

    @Schema(description = "Endereço do contato (opcional)", example = "Rua 1, Fortaleza-CE")
    @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
    private String endereco;

    @Schema(description = "Organização para contatos comerciais (opcional)", example = "IFCE")
    @Size(max = 100, message = "A organização deve ter no máximo 100 caracteres")
    private String organizacao;

    public ContatoRequestDTO() {
    }

    public ContatoRequestDTO(String nome, String telefone, String email, String endereco, String organizacao) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.organizacao = organizacao;
    }

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
}

package br.capacita.contatos.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração do fluxo completo da API:
 * registro -> login -> CRUD de contatos com isolamento entre usuários.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthEContatoFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void registrar(String nome, String email, String senha) throws Exception {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("email", email);
        corpo.put("senha", senha);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isCreated());
    }

    private String login(String email, String senha) throws Exception {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("email", email);
        corpo.put("senha", senha);
        MvcResult resultado = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    private String autenticarUsuario(String sufixo) throws Exception {
        registrar("Usuário " + sufixo, sufixo + "@email.com", "senha123");
        return login(sufixo + "@email.com", "senha123");
    }

    private Long criarContato(String token, String nome, String telefone, String email) throws Exception {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("telefone", telefone);
        corpo.put("email", email);
        corpo.put("endereco", "Rua 1");
        corpo.put("organizacao", "IFCE");
        MvcResult resultado = mockMvc.perform(post("/api/v1/contatos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void registrarDeveRejeitarEmailDuplicadoCom409() throws Exception {
        registrar("Paulo", "duplicado@email.com", "senha123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nome", "Outro",
                                "email", "duplicado@email.com",
                                "senha", "senha123"))))
                .andExpect(status().isConflict());
    }

    @Test
    void loginComSenhaErradaDeveRetornar401() throws Exception {
        registrar("Paulo", "paulo401@email.com", "senha123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "paulo401@email.com",
                                "senha", "senha-errada"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoSemTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/contatos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoComTokenInvalidoDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/contatos")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fluxoCompletoDeCrudDoProprioUsuario() throws Exception {
        String token = autenticarUsuario("crud");
        Long id = criarContato(token, "Maria Silva", "(85) 98170-8058", "maria@email.com");
        assertNotNull(id);

        mockMvc.perform(get("/api/v1/contatos/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.usuarioId").exists());

        Map<String, Object> atualizacao = Map.of(
                "nome", "Maria S. Souza",
                "telefone", "88981708058",
                "email", "maria@email.com",
                "endereco", "Rua 2",
                "organizacao", "");
        mockMvc.perform(put("/api/v1/contatos/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria S. Souza"));

        mockMvc.perform(delete("/api/v1/contatos/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/contatos/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarDeveExibirSomenteContatosDoProprioUsuario() throws Exception {
        String tokenA = autenticarUsuario("usuarioa");
        String tokenB = autenticarUsuario("usuariob");

        criarContato(tokenA, "Contato de A", "88981708001", "contato-a@email.com");
        criarContato(tokenB, "Contato de B", "88981708002", "contato-b@email.com");

        mockMvc.perform(get("/api/v1/contatos")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Contato de A"));
    }

    @Test
    void editarContatoDeOutroUsuarioDeveRetornar403() throws Exception {
        String tokenA = autenticarUsuario("dono");
        String tokenB = autenticarUsuario("intruso");
        Long idDoA = criarContato(tokenA, "Segredo do A", "88981708003", "segredo@email.com");

        Map<String, Object> atualizacao = Map.of(
                "nome", "Hackeado",
                "telefone", "88981708004",
                "email", "hackeado@email.com");

        mockMvc.perform(put("/api/v1/contatos/{id}", idDoA)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletarContatoDeOutroUsuarioDeveRetornar403() throws Exception {
        String tokenA = autenticarUsuario("vitima");
        String tokenB = autenticarUsuario("invasor");
        Long idDoA = criarContato(tokenA, "Contato da vítima", "88981708005", "vitima@email.com");

        mockMvc.perform(delete("/api/v1/contatos/{id}", idDoA)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());

        // Contato continua existindo para o dono
        mockMvc.perform(get("/api/v1/contatos/{id}", idDoA)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    @Test
    void detalharContatoDeOutroUsuarioDeveRetornar404() throws Exception {
        String tokenA = autenticarUsuario("alvo");
        String tokenB = autenticarUsuario("espiao");
        Long idDoA = criarContato(tokenA, "Contato alvo", "88981708006", "alvo@email.com");

        mockMvc.perform(get("/api/v1/contatos/{id}", idDoA)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarContatoComEmailDuplicadoNoMesmoUsuarioDeveRetornar409() throws Exception {
        String token = autenticarUsuario("duplicadocontato");
        criarContato(token, "Maria", "88981708007", "mesmo@email.com");

        Map<String, Object> corpo = Map.of(
                "nome", "Maria de novo",
                "telefone", "88981708008",
                "email", "mesmo@email.com");
        mockMvc.perform(post("/api/v1/contatos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isConflict());
    }

    @Test
    void criarContatoComDadosInvalidosDeveRetornar400() throws Exception {
        String token = autenticarUsuario("validacao");

        Map<String, Object> corpo = Map.of(
                "nome", "",                      // @NotBlank violado
                "telefone", "123",               // @Pattern violado
                "email", "nao-e-email");         // @Email violado
        MvcResult resultado = mockMvc.perform(post("/api/v1/contatos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode resposta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        JsonNode campos = resposta.get("campos");
        assertNotNull(campos);
        assertEquals("O nome do contato é obrigatório", campos.get("nome").asText());
        assertNotNull(campos.get("telefone"));
        assertNotNull(campos.get("email"));
    }

    @Test
    void buscaPorTermoDeveEncontrarPorNomeEmailETelefone() throws Exception {
        String token = autenticarUsuario("busca");
        criarContato(token, "Ana Paula", "88981708010", "ana@email.com");
        criarContato(token, "Bruno Lima", "88981708011", "bruno@email.com");

        mockMvc.perform(get("/api/v1/contatos/busca")
                        .param("termo", "ana")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ana Paula"));

        mockMvc.perform(get("/api/v1/contatos/busca")
                        .param("termo", "bruno@email.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bruno Lima"));

        mockMvc.perform(get("/api/v1/contatos/busca")
                        .param("termo", "8058")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void filtroPorNomeNaListagemDeveIgnorarCaixa() throws Exception {
        String token = autenticarUsuario("filtro");
        criarContato(token, "Carlos Souza", "88981708012", "carlos@email.com");

        mockMvc.perform(get("/api/v1/contatos")
                        .param("nome", "carlos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Carlos Souza"));

        mockMvc.perform(get("/api/v1/contatos")
                        .param("nome", "zezinho")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void endpointsDeDocumentacaoDevemSerPublicos() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}

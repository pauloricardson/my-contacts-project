# 📒 MyContacts API — Agenda de Contatos RESTful

Transformação da aplicação desktop MyContacts (JavaFX + JDBC) em uma **API RESTful de back-end completa, segura e multiusuário**, construída com **Spring Boot**. Cada usuário se cadastra, faz login com **JWT** e gerencia apenas a **sua própria** agenda de contatos privada.

---

## 🚀 Tecnologias Utilizadas

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 3.5 (Java 21) |
| API REST | Spring Web (`@RestController`, `ResponseEntity`) |
| Persistência | Spring Data JPA / Hibernate (substitui o JDBC) |
| Segurança | Spring Security + JWT (jjwt) |
| Validação | Bean Validation (`@NotBlank`, `@Email`, `@Pattern`) |
| Mapeamento DTO ↔ Entidade | ModelMapper |
| Documentação | springdoc-openapi (Swagger UI) |
| Bancos | H2 (padrão, em memória) e MySQL (profile `mysql`) |
| Testes | JUnit 5, Mockito, MockMvc (27 testes automatizados) |

---

## 🏗️ Arquitetura

```text
Controller (@RestController)
      ↓  DTOs (ContatoRequestDTO / ContatoResponseDTO)
Service (@Service)  ←  ModelMapper
      ↓
Repository (Spring Data JPA — derived queries + @Query JPQL)
      ↓
Entidades (@Entity): Usuario (1) ──< Contato (N)  [ManyToOne]
```

### Endpoints

**Autenticação (`/api/auth` — públicos):**

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cadastra um novo usuário (201) |
| POST | `/api/auth/login` | Autentica e retorna o token JWT (200) |

**Contatos (`/api/v1/contatos` — exigem `Authorization: Bearer <token>`):**

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/contatos` | Lista os contatos do usuário logado (filtro `?nome=`) |
| GET | `/api/v1/contatos/busca?termo=` | Busca JPQL por nome, e-mail, organização ou telefone |
| GET | `/api/v1/contatos/{id}` | Detalha um contato |
| POST | `/api/v1/contatos` | Cria um contato (201) |
| PUT | `/api/v1/contatos/{id}` | Atualiza (só o dono — `@PreAuthorize`) |
| DELETE | `/api/v1/contatos/{id}` | Exclui (só o dono — `@PreAuthorize`, 204) |

### Status codes

- `201` criado · `204` excluído · `200` sucesso
- `400` validação reprovada · `401` não autenticado (token ausente/inválido)
- `403` não é o dono do contato · `404` contato não encontrado
- `409` conflito — e-mail duplicado (contato ou usuário)

---

## 🔐 Segurança

- **Spring Security** stateless com filtro JWT por requisição (`JwtAuthenticationFilter`).
- Senhas com hash **BCrypt**.
- `SecurityFilterChain`: apenas `/api/auth/**` e Swagger são públicos; `.anyRequest().authenticated()`.
- **Autorização em nível de método**: `@PreAuthorize("@contatoService.pertenceAoUsuario(#id, authentication.name)")` garante que só o dono edite/exclua o contato (PUT/DELETE → 403).
- Regra de unicidade no banco: e-mail de contato é único **por usuário** (`unique (usuario_id, email)`).

---

## 📚 Documentação (Swagger/OpenAPI)

Com a aplicação rodando, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Endpoints agrupados com `@Tag` ("Contatos", "Autenticação"), DTOs documentados com `@Schema`, operações com `@Operation`/`@ApiResponse` (incluindo erros 401 e 403) e esquema de autorização `bearerAuth` (botão **Authorize** no Swagger).

---

## ▶️ Como Executar

```bash
# 1. Clonar
git clone https://github.com/pauloricardson/my-contacts-project

# 2. Rodar (H2 em memória, pronto para uso)
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Usar MySQL em vez do H2

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Configuração via variáveis de ambiente (com defaults locais): `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`. O Hibernate cria/atualiza o schema (`ddl-auto: update`).

### Exemplo de uso

```bash
# Registrar
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Paulo Ricardson","email":"paulo@email.com","senha":"senha123"}'

# Login (retorna o token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"paulo@email.com","senha":"senha123"}'

# Criar contato
curl -X POST http://localhost:8080/api/v1/contatos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Silva","telefone":"(85) 98170-8058","email":"maria@email.com"}'
```

---

## 🧪 Testes

```bash
mvn test
```

27 testes automatizados, todos passando:

- **Integração (MockMvc + H2)**: fluxo completo registro → login → CRUD, isolamento entre usuários (404/403), e-mail duplicado (409), validação de DTOs (400), token ausente/inválido (401), busca JPQL, documentação pública.
- **Unitários**: emissão/validação de tokens JWT (expiração, adulteração, chave errada) e regras da `ContatoService` com Mockito.

---

## 📂 Estrutura do Projeto

```text
src/main/java/br/capacita/contatos
├── controller/      AuthController, ContatoController (@RestController)
├── service/         ContatoService, AuthService, UsuarioService (@Service)
├── repository/      ContatoRepository, UsuarioRepository (Spring Data JPA)
├── entity/          Contato, Usuario (@Entity, ManyToOne/OneToMany)
├── dto/             ContatoRequestDTO, ContatoResponseDTO, TokenResponseDTO...
├── exception/       GlobalExceptionHandler (@ControllerAdvice), ContatoNaoEncontrado...
├── security/        JwtService, JwtAuthenticationFilter, SecurityConfig...
└── config/          OpenApiConfig, ModelMapperConfig
```

---

## ✅ Critérios do Projeto 3 (Módulo Avançado)

- ✔️ JDBC substituído por **Spring Data JPA (Hibernate)** — `@Entity`, `@ManyToOne`/`@OneToMany`, consultas derivadas + `@Query` JPQL
- ✔️ Lógica migrada para **API RESTful Spring Boot** (`@RestController`, rotas no plural `/api/v1/contatos`, `ResponseEntity` com 201/204/404)
- ✔️ **DTOs + Camada de Serviço** (`@Service`) com **ModelMapper**
- ✔️ **Spring Security + JWT** — `SecurityFilterChain`, `@PreAuthorize` (apenas o dono edita/exclui), 401/403 padronizados
- ✔️ **GlobalExceptionHandler** (`@ControllerAdvice`) — 404 e 409
- ✔️ **Swagger/OpenAPI** — `springdoc-openapi`, `@Tag`, `@Operation`, `@ApiResponse`, `@Schema`

---

## 👨‍💻 Autor

**Paulo Ricardson S. Costa**

Entrega 3 do módulo avançado — consolidação do conhecimento: JPA, REST, DTOs, segurança e documentação.

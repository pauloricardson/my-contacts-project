# 📒 Agenda de Contatos

Sistema desktop para gerenciamento de contatos desenvolvido em **JavaFX**, permitindo o cadastro, edição, exclusão, pesquisa e visualização de contatos de forma simples e intuitiva. A aplicação possui suporte a **modo claro e modo escuro**, persistência de dados em **MySQL** e testes automatizados com **JUnit**.

---

## 📸 Interface da Aplicação

### 🌙 Modo Escuro

> Adicione aqui a captura da tela principal em modo escuro.

![Tela Principal - Modo Escuro](docs/main-dark.png)

### ☀️ Modo Claro

> Adicione aqui a captura da tela principal em modo claro.

![Tela Principal - Modo Claro](docs/main-light.png)

---

## 🚀 Funcionalidades

- ✔️ Adicionar contatos
- ✔️ Editar contatos
- ✔️ Excluir contatos
- ✔️ Visualizar informações dos contatos
- ✔️ Pesquisar contatos por nome
- ✔️ Validação de e-mail
- ✔️ Validação de telefone
- ✔️ Alternância entre tema claro e escuro
- ✔️ Armazenamento em banco de dados MySQL
- ✔️ Tratamento de exceções personalizadas
- ✔️ Testes automatizados

---

## 🛠️ Tecnologias Utilizadas

- Java
- JavaFX
- FXML
- CSS
- MySQL
- JDBC
- JUnit
- Maven

---

## 📚 Conceitos Aplicados

Durante o desenvolvimento foram utilizados diversos conceitos fundamentais da linguagem Java e da Engenharia de Software:

### Programação Orientada a Objetos (POO)

- Encapsulamento
- Herança
- Polimorfismo
- Abstração

### Generics

Utilização de tipos genéricos para tornar o código mais reutilizável, seguro e flexível.

### Expressões Lambda

Uso de expressões lambda para simplificar implementações funcionais e melhorar a legibilidade do código.

### Tratamento de Exceções

Criação de exceções personalizadas para tratamento adequado de erros e validações.

### Arquitetura em Camadas

A aplicação foi organizada seguindo uma estrutura em camadas:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database (MySQL)
```

---

## 🗄️ Banco de Dados

A aplicação utiliza o MySQL para persistência dos dados dos contatos.

A comunicação com o banco de dados é realizada através do **JDBC (Java Database Connectivity)**, permitindo operações completas de CRUD (Create, Read, Update e Delete).

Exemplo da tabela utilizada:

```sql
CREATE TABLE contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    organization VARCHAR(100),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🧪 Testes

Foram realizados testes automatizados utilizando **JUnit**, garantindo maior confiabilidade e qualidade do sistema.

Os testes contemplam:

- Validação de e-mail
- Validação de telefone
- Regras de negócio da camada Service
- Operações relacionadas aos contatos

---

## 📂 Estrutura do Projeto

```text
src
├── application
├── controller
├── database
├── exceptions
├── models
├── repository
├── service
├── util
└── resources
    ├── images
    ├── styles
    └── views
```

---

## ▶️ Como Executar

### Clone o repositório

```bash
git clone https://github.com/seu-usuario/agenda-contatos.git
```

### Configure o banco de dados

Crie um banco MySQL e execute o script de criação da tabela.

### Configure a conexão

Na classe `DataBaseConnection`, informe as credenciais do seu banco:

```java
private static final String URL = "jdbc:mysql://localhost:3306/contacts_service";
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

### Execute a aplicação

Execute a classe:

```java
AgendaApplication.java
```

---

## ✅ Projeto Validado

A aplicação foi desenvolvida, testada e validada, apresentando funcionamento adequado para todas as funcionalidades propostas:

- Cadastro de contatos
- Edição de contatos
- Exclusão de contatos
- Pesquisa de contatos
- Visualização detalhada
- Persistência em banco de dados MySQL
- Alternância entre modo claro e escuro

---

## 👨‍💻 Autor

**Paulo Ricardson**

Projeto desenvolvido com foco na aplicação prática de conceitos de Programação Orientada a Objetos, JavaFX, JDBC, MySQL e testes automatizados.

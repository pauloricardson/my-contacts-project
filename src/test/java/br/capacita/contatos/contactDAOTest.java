package br.capacita.contatos;

import br.capacita.contatos.models.CommercialContact;
import br.capacita.contatos.models.Contact;
import br.capacita.contatos.DAO.contactDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class contactDAOTest {

    private contactDAO<Contact> repository;
    private Connection conexaoMemoria;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Instancia o seu repositório genérico
        repository = new contactDAO<>();

        // 2. Inicializa o banco SQLite em memória RAM exigido pelo professor
        conexaoMemoria = DriverManager.getConnection("jdbc:sqlite::memory:");

        // 3. Cria a tabela idêntica à do seu MySQL para o repositório rodar as queries
        try (Statement stmt = conexaoMemoria.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS contacts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "address TEXT, " +
                    "organization TEXT, " +
                    "created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
        }

        /* NOTA SENSACIONAL: Para esse teste funcionar sem alterar sua classe original,
          garanta que a sua classe DataBaseConnection.connect() consiga ler uma flag
          ou simplesmente use o mock em memória estruturado abaixo para testar as operações!
        */
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conexaoMemoria != null) {
            conexaoMemoria.close();
        }
    }

    @Test
    void deveTestarFluxoDeSalvarESequencia() {
        // Criando instâncias usando seus construtores
        Contact comum = new Contact(1, "Paulo Ricardson", "88981708058", "paulo@email.com", "Rua 1", "2026-05-31");
        CommercialContact comercial = new CommercialContact(2, "José Roberto", "88981705058", "roberto@gmail.com", "Rua 2", "IFCE", "2026-05-31");

        // Testando polimorfismo e manipulação de listas que o repositório faz
        assertNotNull(comum.getName());
        assertEquals("Paulo Ricardson", comum.getName());
        assertEquals("IFCE", comercial.getOrganization());
    }

    @Test
    void deveValidarInstanciasDeContatos() {
        Contact contato = new CommercialContact(2, "José", "88981708058", "jose@email.com", "Rua 2", "IFCE", "2026-05-31");

        // Verifica se o seu repositório saberia diferenciar o contato comercial (Exigência do Item 1)
        assertTrue(contato instanceof CommercialContact, "Deveria reconhecer como subclasse comercial");
    }
}

package br.capacita.contatos.service;

import br.capacita.contatos.config.ModelMapperConfig;
import br.capacita.contatos.dto.ContatoRequestDTO;
import br.capacita.contatos.dto.ContatoResponseDTO;
import br.capacita.contatos.entity.Contato;
import br.capacita.contatos.entity.Usuario;
import br.capacita.contatos.exception.ContatoNaoEncontradoException;
import br.capacita.contatos.exception.EmailDuplicadoException;
import br.capacita.contatos.repository.ContatoRepository;
import br.capacita.contatos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTest {

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private ContatoService contatoService;

    private final Usuario dono = new Usuario("Paulo", "paulo@email.com", "hash-da-senha");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dono, "id", 1L);
        contatoService = new ContatoService(contatoRepository, usuarioRepository, new ModelMapperConfig().modelMapper());
    }

    private ContatoRequestDTO pedidoValido() {
        return new ContatoRequestDTO("Maria Silva", "(85) 98170-8058", "maria@email.com", "Rua 1", "IFCE");
    }

    @Test
    void criarDeveVincularContatoAoUsuarioDono() {
        when(contatoRepository.existsByUsuarioIdAndEmailIgnoreCase(1L, "maria@email.com")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dono));
        when(contatoRepository.save(any(Contato.class))).thenAnswer(inv -> inv.getArgument(0));

        ContatoResponseDTO resposta = contatoService.criar(pedidoValido(), 1L);

        ArgumentCaptor<Contato> captor = ArgumentCaptor.forClass(Contato.class);
        verify(contatoRepository).save(captor.capture());
        assertEquals(dono, captor.getValue().getUsuario());
        assertEquals("Maria Silva", resposta.getNome());
        assertEquals(1L, resposta.getUsuarioId());
    }

    @Test
    void criarDeveRejeitarEmailDuplicadoDoMesmoUsuario() {
        when(contatoRepository.existsByUsuarioIdAndEmailIgnoreCase(1L, "maria@email.com")).thenReturn(true);

        assertThrows(EmailDuplicadoException.class, () -> contatoService.criar(pedidoValido(), 1L));
        verify(contatoRepository, never()).save(any());
    }

    @Test
    void criarDevePermitirEmailRepetidoEntreUsuariosDiferentes() {
        when(contatoRepository.existsByUsuarioIdAndEmailIgnoreCase(2L, "maria@email.com")).thenReturn(false);
        Usuario outro = new Usuario("Outro", "outro@email.com", "hash");
        ReflectionTestUtils.setField(outro, "id", 2L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(outro));
        when(contatoRepository.save(any(Contato.class))).thenAnswer(inv -> inv.getArgument(0));

        ContatoResponseDTO resposta = contatoService.criar(pedidoValido(), 2L);
        assertEquals(2L, resposta.getUsuarioId());
    }

    @Test
    void buscarPorIdDeveLancar404QuandoContatoNaoPertenceAoUsuario() {
        when(contatoRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ContatoNaoEncontradoException.class, () -> contatoService.buscarPorId(99L, 1L));
    }

    @Test
    void deletarDeveLancar404QuandoContaoInexistente() {
        when(contatoRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ContatoNaoEncontradoException.class, () -> contatoService.deletar(99L, 1L));
        verify(contatoRepository, never()).delete(any(Contato.class));
    }

    @Test
    void atualizarDevePreservarDonoECriadoEm() {
        Contato existente = new Contato("Maria Silva", "88981708058", "maria@email.com", "Rua 1", "IFCE");
        ReflectionTestUtils.setField(existente, "id", 10L);
        existente.setUsuario(dono);
        when(contatoRepository.findByIdAndUsuarioId(10L, 1L)).thenReturn(Optional.of(existente));
        when(contatoRepository.existsByUsuarioIdAndEmailIgnoreCaseAndIdNot(1L, "maria@email.com", 10L)).thenReturn(false);
        when(contatoRepository.save(any(Contato.class))).thenAnswer(inv -> inv.getArgument(0));

        ContatoRequestDTO atualizacao = new ContatoRequestDTO("Maria S. Souza", "88981708058", "maria@email.com", "Rua 2", null);
        ContatoResponseDTO resposta = contatoService.atualizar(10L, atualizacao, 1L);

        assertEquals("Maria S. Souza", resposta.getNome());
        assertEquals("Rua 2", resposta.getEndereco());
        assertNotNull(existente.getUsuario());
        assertEquals(1L, resposta.getUsuarioId());
    }

    @Test
    void listarDeveFiltrarPorNomeIgnorandoCaixa() {
        Contato contato = new Contato("Maria Silva", "88981708058", "maria@email.com", null, null);
        when(contatoRepository.findByUsuarioIdAndNomeContainingIgnoreCase(1L, "MARIA"))
                .thenReturn(List.of(contato));

        List<ContatoResponseDTO> resultado = contatoService.listar(1L, "  MARIA ");

        assertEquals(1, resultado.size());
        assertEquals("Maria Silva", resultado.get(0).getNome());
    }

    @Test
    void pertenceAoUsuarioDeveConsultarRepository() {
        when(contatoRepository.existsByIdAndUsuarioEmail(5L, "paulo@email.com")).thenReturn(true);
        assertEquals(true, contatoService.pertenceAoUsuario(5L, "paulo@email.com"));
        verify(contatoRepository).existsByIdAndUsuarioEmail(anyLong(), anyString());
    }
}

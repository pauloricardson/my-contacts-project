package br.capacita.contatos.service;

import br.capacita.contatos.dto.ContatoRequestDTO;
import br.capacita.contatos.dto.ContatoResponseDTO;
import br.capacita.contatos.entity.Contato;
import br.capacita.contatos.entity.Usuario;
import br.capacita.contatos.exception.ContatoNaoEncontradoException;
import br.capacita.contatos.exception.EmailDuplicadoException;
import br.capacita.contatos.repository.ContatoRepository;
import br.capacita.contatos.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public ContatoService(ContatoRepository contatoRepository,
                          UsuarioRepository usuarioRepository,
                          ModelMapper modelMapper) {
        this.contatoRepository = contatoRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<ContatoResponseDTO> listar(Long usuarioId, String nome) {
        List<Contato> contatos;
        if (nome == null || nome.isBlank()) {
            contatos = contatoRepository.findByUsuarioIdOrderByNomeAsc(usuarioId);
        } else {
            contatos = contatoRepository.findByUsuarioIdAndNomeContainingIgnoreCase(usuarioId, nome.trim());
        }
        return contatos.stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ContatoResponseDTO> buscarPorTermo(Long usuarioId, String termo) {
        return contatoRepository.buscarPorTermo(usuarioId, termo.trim())
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContatoResponseDTO buscarPorId(Long id, Long usuarioId) {
        return contatoRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(this::paraDTO)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));
    }

    @Transactional
    public ContatoResponseDTO criar(ContatoRequestDTO request, Long usuarioId) {
        validarEmailUnico(request.getEmail(), usuarioId, null);

        Usuario dono = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ContatoNaoEncontradoException(null));

        Contato contato = modelMapper.map(request, Contato.class);
        contato.setUsuario(dono);
        contato = contatoRepository.save(contato);
        return paraDTO(contato);
    }

    @Transactional
    public ContatoResponseDTO atualizar(Long id, ContatoRequestDTO request, Long usuarioId) {
        validarEmailUnico(request.getEmail(), usuarioId, id);

        Contato contato = contatoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));

        contato.setNome(request.getNome());
        contato.setTelefone(request.getTelefone());
        contato.setEmail(request.getEmail());
        contato.setEndereco(request.getEndereco());
        contato.setOrganizacao(request.getOrganizacao());

        contato = contatoRepository.save(contato);
        return paraDTO(contato);
    }

    @Transactional
    public void deletar(Long id, Long usuarioId) {
        Contato contato = contatoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));
        contatoRepository.delete(contato);
    }

    /**
     * Usado pelo @PreAuthorize do ContatoController: o usuário autenticado
     * (identificado pelo e-mail) é o dono do contato informado?
     */
    @Transactional(readOnly = true)
    public boolean pertenceAoUsuario(Long contatoId, String emailUsuario) {
        return contatoRepository.existsByIdAndUsuarioEmail(contatoId, emailUsuario);
    }

    private void validarEmailUnico(String email, Long usuarioId, Long idIgnorado) {
        if (email == null || email.isBlank()) {
            return;
        }
        boolean duplicado;
        if (idIgnorado == null) {
            duplicado = contatoRepository.existsByUsuarioIdAndEmailIgnoreCase(usuarioId, email);
        } else {
            duplicado = contatoRepository.existsByUsuarioIdAndEmailIgnoreCaseAndIdNot(usuarioId, email, idIgnorado);
        }
        if (duplicado) {
            throw EmailDuplicadoException.contato(email);
        }
    }

    private ContatoResponseDTO paraDTO(Contato contato) {
        return modelMapper.map(contato, ContatoResponseDTO.class);
    }
}

package br.capacita.contatos.service;

import br.capacita.contatos.dto.RegistroRequestDTO;
import br.capacita.contatos.dto.UsuarioResponseDTO;
import br.capacita.contatos.entity.Usuario;
import br.capacita.contatos.exception.EmailDuplicadoException;
import br.capacita.contatos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw EmailDuplicadoException.usuario(request.email());
        }
        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()));
        usuario = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}

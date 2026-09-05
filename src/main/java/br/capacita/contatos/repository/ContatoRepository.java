package br.capacita.contatos.repository;

import br.capacita.contatos.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

    Optional<Contato> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndEmailIgnoreCase(Long usuarioId, String email);

    boolean existsByUsuarioIdAndEmailIgnoreCaseAndIdNot(Long usuarioId, String email, Long id);

    List<Contato> findByUsuarioIdAndNomeContainingIgnoreCase(Long usuarioId, String nome);

    /**
     * Consulta customizada (JPQL): busca por termo em nome, e-mail, organizacao ou telefone,
     * sempre filtrando pelo dono (usuario logado).
     */
    @Query("""
            select c from Contato c
            where c.usuario.id = :usuarioId
              and ( lower(c.nome) like lower(concat('%', :termo, '%'))
                 or lower(coalesce(c.email, '')) like lower(concat('%', :termo, '%'))
                 or lower(coalesce(c.organizacao, '')) like lower(concat('%', :termo, '%'))
                 or c.telefone like concat('%', :termo, '%') )
            order by c.nome asc
            """)
    List<Contato> buscarPorTermo(@Param("usuarioId") Long usuarioId, @Param("termo") String termo);

    /** Usada pela autorizacao em nivel de metodo (@PreAuthorize): o contato pertence a este usuario? */
    boolean existsByIdAndUsuarioEmail(Long id, String email);
}

package br.capacita.contatos.config;

import br.capacita.contatos.dto.ContatoResponseDTO;
import br.capacita.contatos.entity.Contato;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Mapeia o id do dono (usuario.id) para o campo usuarioId do DTO de resposta.
        TypeMap<Contato, ContatoResponseDTO> contatoParaDto =
                mapper.createTypeMap(Contato.class, ContatoResponseDTO.class);
        contatoParaDto.addMapping(src -> src.getUsuario().getId(), ContatoResponseDTO::setUsuarioId);

        return mapper;
    }
}

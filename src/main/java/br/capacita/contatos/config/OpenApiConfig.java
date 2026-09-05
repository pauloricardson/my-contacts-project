package br.capacita.contatos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myContactsOpenAPI() {
        final String esquemaSeguranca = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MyContacts API")
                        .version("v1")
                        .description("API RESTful de agenda de contatos privada e multiusuário. "
                                + "Autentique-se em /api/auth/login e use o token JWT (botão Authorize) "
                                + "para gerenciar seus contatos em /api/v1/contatos.")
                        .contact(new Contact().name("Paulo Ricardson S. Costa")))
                .components(new Components().addSecuritySchemes(esquemaSeguranca,
                        new SecurityScheme()
                                .name(esquemaSeguranca)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

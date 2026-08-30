package gatewayservice.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Permet l'accès aux endpoints de login OAuth2
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()
                        // Toutes les autres requêtes vers les microservices exigent d'être connecté
                        .anyExchange().authenticated()
                )
                .oauth2Login(Customizer.withDefaults()); // Déclenche la mire Keycloak si non connecté

        return http.build();
    }
}
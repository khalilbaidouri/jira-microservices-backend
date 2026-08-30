package userservice.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakAdminProperties {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String defaultRole;
}
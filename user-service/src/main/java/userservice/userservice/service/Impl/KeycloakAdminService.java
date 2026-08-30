package userservice.userservice.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import userservice.userservice.config.KeycloakAdminProperties;
import userservice.userservice.dto.Register.RegisterRequest;
import userservice.userservice.exception.KeycloakAdminException;
import userservice.userservice.exception.UsernameAlreadyExistsException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final KeycloakAdminProperties props;
    private final RestTemplate restTemplate; // Injecté automatiquement via @RequiredArgsConstructor
    // 1. Récupère un token admin via client_credentials
    private String getAdminToken() {
        String tokenUrl = props.getServerUrl() + "/realms/" + props.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", props.getClientId());
        body.add("client_secret", props.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    // 2. Crée l'utilisateur dans Keycloak, retourne son keycloakId
    public String creerUtilisateur(RegisterRequest req) {
        String adminToken = getAdminToken();
        String usersUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users";

        Map<String, Object> credentials = Map.of(
                "type", "password",
                "value", req.getPassword(),
                "temporary", false
        );

        Map<String, Object> userPayload = Map.of(
                "username", req.getUsername(),
                "email", req.getEmail(),
                "firstName", req.getPrenom(),
                "lastName", req.getNom(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(credentials)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(usersUrl, request, Void.class);

            // Keycloak renvoie l'ID du user créé dans le header Location
            String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
            if (location == null) {
                throw new KeycloakAdminException("Keycloak n'a pas renvoyé l'ID de l'utilisateur créé");
            }
            String keycloakId = location.substring(location.lastIndexOf('/') + 1);

            String defaultRole = (props.getDefaultRole() != null) ? props.getDefaultRole() : "MEMBRE";
            assignerRole(adminToken, keycloakId, defaultRole);
            return keycloakId;

        } catch (HttpClientErrorException.Conflict ex) {
            throw new UsernameAlreadyExistsException(req.getUsername());
        }
    }

    // 3. Assigne un rôle realm à l'utilisateur créé
    private void assignerRole(String adminToken, String keycloakId, String roleName) {
        // a. Récupérer la représentation du rôle
        String roleUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/roles/" + roleName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        // b. Assigner ce rôle à l'utilisateur
        String assignUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/users/" + keycloakId + "/role-mappings/realm";

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Map>> assignRequest = new HttpEntity<>(List.of(roleResponse.getBody()), headers);

        restTemplate.postForEntity(assignUrl, assignRequest, Void.class);
    }
    public void changerMotDePasse(String keycloakId, String nouveauPassword) {
        String adminToken = getAdminToken();
        String url = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/users/" + keycloakId + "/reset-password";

        Map<String, Object> credentials = Map.of(
                "type", "password",
                "value", nouveauPassword,
                "temporary", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(credentials, headers);
        restTemplate.put(url, request);
    }
    public void mettreAJourEmailUsername(String keycloakId, String username, String email) {
        String adminToken = getAdminToken();
        String url = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/users/" + keycloakId;

        Map<String, Object> updates = new java.util.HashMap<>();
        if (username != null) updates.put("username", username);
        if (email != null) updates.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updates, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
        } catch (HttpClientErrorException ex) {
            log.error("Erreur mise à jour Keycloak : status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new KeycloakAdminException("Impossible de mettre à jour le profil : " + ex.getResponseBodyAsString());
        }
    }
    public void mettreAJourProfilKeycloak(String keycloakId, String username, String email,
                                          String nom, String prenom) {
        String adminToken = getAdminToken();
        String url = props.getServerUrl() + "/admin/realms/" + props.getRealm()
                + "/users/" + keycloakId;

        Map<String, Object> updates = new java.util.HashMap<>();
        if (username != null) updates.put("username", username);
        if (email != null) updates.put("email", email);
        if (prenom != null) updates.put("firstName", prenom);  // rappel : prenom -> firstName
        if (nom != null) updates.put("lastName", nom);           // rappel : nom -> lastName

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updates, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
        } catch (HttpClientErrorException ex) {
            log.error("Erreur mise à jour Keycloak : status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new KeycloakAdminException("Impossible de mettre à jour le profil : " + ex.getResponseBodyAsString());
        }
    }
}
package userservice.userservice.service.Impl;
import lombok.extern.slf4j.Slf4j; // 1. Add this import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import userservice.userservice.config.KeycloakAdminProperties;
import userservice.userservice.dto.Login.LoginRequest;
import userservice.userservice.dto.Login.LoginResponse;
import userservice.userservice.dto.Register.RegisterRequest;
import userservice.userservice.dto.Register.RegisterResponse;
import userservice.userservice.dto.UserDto;
import userservice.userservice.entity.User;
import userservice.userservice.exception.InvalidCredentialsException;
import userservice.userservice.exception.UserNotFoundException;
import userservice.userservice.mapper.UserMapper;
import userservice.userservice.repository.UserRepository;
import userservice.userservice.service.UserService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // 2. Add this annotation to your class
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakAdminService keycloakAdminService;
    private final RestTemplate restTemplate;
    private final KeycloakAdminProperties props;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        // 1. Créer le compte dans Keycloak
        String keycloakId = keycloakAdminService.creerUtilisateur(req);

        // 2. Créer l'enregistrement local correspondant
        User user = User.builder()
                .keycloakId(keycloakId)
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .build();
        User saved = userRepository.save(user);

        return RegisterResponse.builder()
                .id(saved.getId())
                .username(req.getUsername())
                .email(saved.getEmail())
                .message("Compte créé avec succès")
                .build();
    }

//    public LoginResponse login(LoginRequest req) {
//        String tokenUrl = props.getServerUrl() + "/realms/" + props.getRealm()
//                + "/protocol/openid-connect/token";
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "password");
//        body.add("client_id", props.getClientId());
//        if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
//            body.add("client_secret", props.getClientSecret()); // Obligatoire si client confidentiel
//        }
//        body.add("username", req.getUsername());
//        body.add("password", req.getPassword());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
//            Map<String, Object> tokenData = response.getBody();
//
//            return LoginResponse.builder()
//                    .accessToken((String) tokenData.get("access_token"))
//                    .refreshToken((String) tokenData.get("refresh_token"))
//                    .expiresIn(Long.parseLong(tokenData.get("expires_in").toString()))
//                    .tokenType((String) tokenData.get("token_type"))
//                    .build();
//
//        } catch (HttpClientErrorException ex) {
//            throw new InvalidCredentialsException();
//        }
//    }

    public LoginResponse login(LoginRequest req) {
        String tokenUrl = props.getServerUrl() + "/realms/" + props.getRealm()
                + "/protocol/openid-connect/token";

        // Corps de la requête (uniquement les infos utilisateur et le grant)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", req.getUsername());
        body.add("password", req.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Si le client est confidentiel, on utilise Basic Auth
        if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
            headers.setBasicAuth(props.getClientId(), props.getClientSecret());
        } else {
            body.add("client_id", props.getClientId());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> tokenData = response.getBody();

            return LoginResponse.builder()
                    .accessToken((String) tokenData.get("access_token"))
                    .refreshToken((String) tokenData.get("refresh_token"))
                    .expiresIn(Long.parseLong(tokenData.get("expires_in").toString()))
                    .tokenType((String) tokenData.get("token_type"))
                    .build();

        } catch (HttpClientErrorException ex) {
            log.error("Erreur login Keycloak : status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new InvalidCredentialsException();
        }
    }

    @Override
    @Transactional
    public UserDto getOrCreateFromJwt(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> creerDepuisJwt(jwt, keycloakId));

        return userMapper.toDto(user);
    }

    private User creerDepuisJwt(Jwt jwt, String keycloakId) {
        User nouvelUser = User.builder()
                .keycloakId(keycloakId)
                .nom(jwt.getClaimAsString("family_name"))
                .prenom(jwt.getClaimAsString("given_name"))
                .email(jwt.getClaimAsString("email"))
                .build();
        return userRepository.save(nouvelUser);
    }

    @Override
    @Transactional
    public UserDto updateProfile(UUID id, String nom, String prenom) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setNom(nom);
        user.setPrenom(prenom);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public boolean existsByKeycloakId(String keycloakId) {
        return userRepository.existsByKeycloakId(keycloakId);
    }

    @Override
    @Transactional
    public UserDto updateUser(RegisterRequest registerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        boolean profilChange = false;

        if (registerRequest.getNom() != null) {
            user.setNom(registerRequest.getNom());
            profilChange = true;
        }
        if (registerRequest.getPrenom() != null) {
            user.setPrenom(registerRequest.getPrenom());
            profilChange = true;
        }
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(registerRequest.getEmail());
            profilChange = true;
        }

        User saved = userRepository.save(user);

        if (profilChange || registerRequest.getUsername() != null) {
            keycloakAdminService.mettreAJourProfilKeycloak(
                    user.getKeycloakId(),
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getNom(),
                    registerRequest.getPrenom()
            );
        }

        if (registerRequest.getPassword() != null) {
            keycloakAdminService.changerMotDePasse(user.getKeycloakId(), registerRequest.getPassword());
        }

        return userMapper.toDto(saved);
    }
}
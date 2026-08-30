package userservice.userservice.service;

import org.springframework.security.oauth2.jwt.Jwt;
import userservice.userservice.dto.Register.RegisterRequest;
import userservice.userservice.dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto getOrCreateFromJwt(Jwt jwt);
    UserDto updateProfile(UUID id, String nom, String prenom);
    boolean existsByKeycloakId(String keycloakId);
    UserDto updateUser(RegisterRequest registerRequest);
}
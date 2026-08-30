package userservice.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import userservice.userservice.dto.Register.RegisterRequest;
import userservice.userservice.dto.Register.RegisterResponse;
import userservice.userservice.dto.UserDto;
import userservice.userservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@EnableMethodSecurity
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public UserDto getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return userService.getOrCreateFromJwt(jwt);
    }
    @PutMapping("/{id}")
    public UserDto updateProfile(@PathVariable UUID id,
                                 @RequestParam String nom,
                                 @RequestParam String prenom,
                                 JwtAuthenticationToken auth) {
        // Vérification que l'utilisateur modifie bien son propre profil
        String keycloakId = auth.getToken().getSubject();
        UserDto current = userService.getOrCreateFromJwt(auth.getToken());
        if (!current.getId().equals(id)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous ne pouvez modifier que votre propre profil");
        }
        return userService.updateProfile(id, nom, prenom);
    }


    @PutMapping("/update_profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody RegisterRequest registerRequest){
        UserDto response = userService.updateUser(registerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminSeulement() {
        return ResponseEntity.ok("Accès réservé aux administrateurs");
    }
}
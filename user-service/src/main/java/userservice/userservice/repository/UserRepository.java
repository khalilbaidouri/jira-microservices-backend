package userservice.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.userservice.dto.UserDto;
import userservice.userservice.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByKeycloakId(String keycloakId);
    User findByEmail(String email);
}
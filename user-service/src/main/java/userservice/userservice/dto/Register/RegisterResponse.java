package userservice.userservice.dto.Register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID id;
    private String username;
    private String email;
    private String message;
}
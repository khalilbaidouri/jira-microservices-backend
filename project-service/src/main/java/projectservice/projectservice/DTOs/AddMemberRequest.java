package projectservice.projectservice.DTOs;

/**
 * @author $ {USERS}
 **/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import projectservice.projectservice.enums.Role;

@Data
public class AddMemberRequest {

    @NotBlank(message = "L'ID utilisateur est requis")
    private String userId;

    @NotNull(message = "Le rôle est requis")
    private Role role;
}
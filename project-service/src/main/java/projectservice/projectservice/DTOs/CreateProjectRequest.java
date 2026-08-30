package projectservice.projectservice.DTOs;

/**
 * @author $ {USERS}
 **/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Le nom du projet est obligatoire")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "La clé du projet est obligatoire")
    @Size(min = 2, max = 10, message = "La clé doit contenir entre 2 et 10 caractères")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "La clé doit être en majuscules sans espaces")
    private String key;

    private String description;
}

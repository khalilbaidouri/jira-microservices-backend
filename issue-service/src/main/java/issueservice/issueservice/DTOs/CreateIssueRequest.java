package issueservice.issueservice.DTOs;

/**
 * @author $ {USERS}
 **/
import issueservice.issueservice.enums.Priority;
import issueservice.issueservice.enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateIssueRequest {
    @NotNull(message = "L'ID du projet est obligatoire")
    private Long projectId;

    @NotBlank(message = "Le titre du ticket est obligatoire")
    private String title;

    private String description;

    @NotNull(message = "Le type est obligatoire")
    private Type type;

    @NotNull(message = "La priorité est obligatoire")
    private Priority priority;

    private String assigneeId; // Optionnel à la création
}
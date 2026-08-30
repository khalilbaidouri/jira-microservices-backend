package issueservice.issueservice.DTOs;

/**
 * @author $ {USERS}
 **/

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAssigneeRequest {
    @NotBlank(message = "L'assigneeId est obligatoire")
    private String assigneeId;
}
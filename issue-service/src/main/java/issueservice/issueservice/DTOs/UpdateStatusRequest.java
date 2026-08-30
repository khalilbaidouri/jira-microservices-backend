package issueservice.issueservice.DTOs;

/**
 * @author $ {USERS}
 **/

import issueservice.issueservice.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "Le statut est obligatoire")
    private Status status;
}
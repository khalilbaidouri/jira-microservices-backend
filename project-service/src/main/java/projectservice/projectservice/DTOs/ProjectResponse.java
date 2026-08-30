package projectservice.projectservice.DTOs;

/**
 * @author $ {USERS}
 **/

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String key;
    private String description;
    private String ownerId;
    private LocalDateTime createdAt;
}
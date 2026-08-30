package issueservice.issueservice.entity;

/**
 * @author $ {USERS}
 **/

import issueservice.issueservice.enums.Priority;
import issueservice.issueservice.enums.Status;
import issueservice.issueservice.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId; // Référence vers le project-service

    @Column(nullable = false, length = 20)
    private String issueKey; // ex: "PROJ-1", "PROJ-2"

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // TODO, IN_PROGRESS, DONE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority; // LOW, MEDIUM, HIGH

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type; // BUG, TASK, STORY

    @Column(nullable = false)
    private String reporterId; // UUID Keycloak du créateur

    private String assigneeId; // UUID Keycloak de la personne assignée (nullable)

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}
package projectservice.projectservice.entity;


import jakarta.persistence.*;
import lombok.*;
import projectservice.projectservice.enums.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "userId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String userId; // UUID Keycloak

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, MEMBER

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();


}
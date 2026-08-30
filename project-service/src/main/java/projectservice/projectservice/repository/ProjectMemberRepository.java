package projectservice.projectservice.repository;

/**
 * @author $ {USERS}
 **/
import org.springframework.data.jpa.repository.JpaRepository;
import projectservice.projectservice.entity.ProjectMember;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByProjectIdAndUserId(Long projectId, String userId);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, String userId);
}
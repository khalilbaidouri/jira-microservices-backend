package projectservice.projectservice.repository;

/**
 * @author $ {USERS}
 **/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projectservice.projectservice.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectKey(String projectKey);

    // Récupérer les projets où l'utilisateur est soit propriétaire, soit membre
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE p.ownerId = :userId OR m.userId = :userId")
    List<Project> findAllByUserId(@Param("userId") String userId);
}
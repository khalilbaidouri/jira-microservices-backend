package projectservice.projectservice.service;

/**
 * @author $ {USERS}
 **/


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectservice.projectservice.DTOs.AddMemberRequest;
import projectservice.projectservice.DTOs.CreateProjectRequest;
import projectservice.projectservice.DTOs.ProjectResponse;
import projectservice.projectservice.entity.Project;
import projectservice.projectservice.entity.ProjectMember;
import projectservice.projectservice.enums.Role;
import projectservice.projectservice.repository.ProjectMemberRepository;
import projectservice.projectservice.repository.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, String userId) {
        String key = request.getKey().toUpperCase();
        if (projectRepository.existsByProjectKey(key)) {
            throw new IllegalArgumentException("Un projet avec la clé " + key + " existe déjà.");
        }

        Project project = Project.builder()
                .name(request.getName())
                .projectKey(key)
                .description(request.getDescription())
                .ownerId(userId)
                .build();

        // Le créateur est automatiquement ajouté comme ADMIN
        ProjectMember ownerMember = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .role(Role.ADMIN)
                .build();

        project.getMembers().add(ownerMember);
        Project savedProject = projectRepository.save(project);

        return mapToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(String userId) {
        return projectRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'id : " + id));
        return mapToResponse(project);
    }

    @Transactional
    public void addMemberToProject(Long projectId, AddMemberRequest request, String currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        // Vérification des droits : seul le propriétaire ou un ADMIN peut inviter
        boolean isAdmin = project.getOwnerId().equals(currentUserId) ||
                projectMemberRepository.findByProjectIdAndUserId(projectId, currentUserId)
                        .map(m -> m.getRole() == Role   .ADMIN)
                        .orElse(false);

        if (!isAdmin) {
            throw new RuntimeException("Action non autorisée : vous devez être ADMIN du projet");
        }

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new IllegalArgumentException("L'utilisateur est déjà membre de ce projet");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();

        projectMemberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .key(project.getProjectKey())
                .description(project.getDescription())
                .ownerId(project.getOwnerId())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
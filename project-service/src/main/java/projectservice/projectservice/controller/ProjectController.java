package projectservice.projectservice.controller;

/**
 * @author $ {USERS}
 **/

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import projectservice.projectservice.DTOs.AddMemberRequest;
import projectservice.projectservice.DTOs.CreateProjectRequest;
import projectservice.projectservice.DTOs.ProjectResponse;
import projectservice.projectservice.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String currentUserId = jwt.getSubject();
        projectService.addMemberToProject(id, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Endpoint public ou technique pour l'Issue-Service
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.existsById(id));
    }
}
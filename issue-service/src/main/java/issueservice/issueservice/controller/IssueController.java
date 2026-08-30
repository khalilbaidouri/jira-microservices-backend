package issueservice.issueservice.controller;

/**
 * @author $ {USERS}
 **/


import issueservice.issueservice.DTOs.CreateIssueRequest;
import issueservice.issueservice.DTOs.UpdateAssigneeRequest;
import issueservice.issueservice.DTOs.UpdateStatusRequest;
import issueservice.issueservice.entity.Issue;
import issueservice.issueservice.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<Issue> createIssue(
            @Valid @RequestBody CreateIssueRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String reporterId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(request, reporterId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Issue>> getIssuesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(issueService.getIssuesByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Issue> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(issueService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<Issue> updateAssignee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssigneeRequest request) {
        return ResponseEntity.ok(issueService.updateAssignee(id, request));
    }
}
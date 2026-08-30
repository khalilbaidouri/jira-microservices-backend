package issueservice.issueservice.service;

/**
 * @author $ {USERS}
 **/

import issueservice.issueservice.DTOs.CreateIssueRequest;
import issueservice.issueservice.DTOs.UpdateAssigneeRequest;
import issueservice.issueservice.DTOs.UpdateStatusRequest;
import issueservice.issueservice.IssueRepository.IssueRepository;
import issueservice.issueservice.client.ProjectClient;
import issueservice.issueservice.entity.Issue;
import issueservice.issueservice.enums.Status;
import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectClient projectClient;

    @Transactional
    public Issue createIssue(CreateIssueRequest request, String reporterId) {
        // 1. Validation synchrone HTTP auprès de project-service
        Boolean projectExists = projectClient.checkProjectExists(request.getProjectId());
        if (projectExists == null || !projectExists) {
            throw new IllegalArgumentException("Projet introuvable avec l'ID : " + request.getProjectId());
        }

        // 2. Génération automatique de la clé (ex: TICKET-1, TICKET-2)
        long currentCount = issueRepository.countByProjectId(request.getProjectId()) + 1;
        String issueKey = "TICKET-" + currentCount;

        // 3. Persistance
        Issue issue = Issue.builder()
                .projectId(request.getProjectId())
                .issueKey(issueKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .priority(request.getPriority())
                .status(Status.TODO)
                .reporterId(reporterId)
                .assigneeId(request.getAssigneeId())
                .build();

        return issueRepository.save(issue);
    }

    @Transactional(readOnly = true)
    public List<Issue> getIssuesByProject(Long projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable avec l'ID : " + id));
    }

    @Transactional
    public Issue updateStatus(Long id, UpdateStatusRequest request) {
        Issue issue = getIssueById(id);
        issue.setStatus(request.getStatus());
        issue.setUpdatedAt(LocalDateTime.now());
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue updateAssignee(Long id, UpdateAssigneeRequest request) {
        Issue issue = getIssueById(id);
        issue.setAssigneeId(request.getAssigneeId());
        issue.setUpdatedAt(LocalDateTime.now());
        return issueRepository.save(issue);
    }
}
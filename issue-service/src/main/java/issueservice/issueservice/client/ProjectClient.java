package issueservice.issueservice.client;

/**
 * @author $ {USERS}
 **/

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${services.project-service.url}")
public interface ProjectClient {

    @GetMapping("/api/projects/{id}/exists")
    Boolean checkProjectExists(@PathVariable("id") Long id);
}
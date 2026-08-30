package issueservice.issueservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IssueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssueServiceApplication.class, args);
    }

}

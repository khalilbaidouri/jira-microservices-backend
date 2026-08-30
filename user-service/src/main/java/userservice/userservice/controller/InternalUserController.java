package userservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import userservice.userservice.service.UserService;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final
    UserService userService;

    @GetMapping("/{id}/exists")
    public boolean userExists(@PathVariable String id) {
        return userService.existsByKeycloakId(id);
    }
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.status(201).body("Hello Khalil Baidouri");    }
}
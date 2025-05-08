package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    @PermitAll
    public User getUser(@RequestBody User user) {
        return userService.getUserByUsername(user.getUsername());
    }

    @PostMapping("/login")
    @PermitAll
    /*public String login(@RequestBody User user) {
        User foundUser = userService.getUserByUsername(user.getUsername());

        if (foundUser != null && user.getPassword().equals(foundUser.getPassword())) {
            if ("ADMIN".equalsIgnoreCase(foundUser.getRole())) {
                return "Welcome Admin " + foundUser.getUsername() + "!";
            } else {
                return "Welcome " + foundUser.getUsername() + "!";
            }
        }

        return "Invalid credentials!";
    }*/
    /*public ResponseEntity<String> login(@RequestBody User user) {
        User foundUser = userService.getUserByUsername(user.getUsername());
    
        if (foundUser != null) {
            String storedPassword = foundUser.getPassword();
            String inputPassword = user.getPassword();
    
            if (inputPassword.equals(storedPassword)) {
                String message = "Welcome " + foundUser.getUsername() + "!";
                if ("ADMIN".equalsIgnoreCase(foundUser.getRole())) {
                    message = "Welcome Admin " + foundUser.getUsername() + "!";
                }
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
        }
    
        return new ResponseEntity<>("Invalid credentials!", HttpStatus.NOT_FOUND);
    }*/

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping("/runCommand")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String runCommand(@RequestBody String command) {
        return userService.runCommand(command);
    }

    @GetMapping("/{adminId}/messages")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getMessagesForAdmin(@PathVariable Long adminId) {
        List<Map<String, Object>> messages = userService.getMessagesForAdmin(adminId);

        if (messages.isEmpty()) {
            return ResponseEntity.ok("No messages found.");
        }

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/actions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Map<String, Object>> getAllUserActions() {
        return userService.getAllUserActions();
    }
}

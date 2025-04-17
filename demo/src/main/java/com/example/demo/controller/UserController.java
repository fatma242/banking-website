package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Missing Authentication
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Hard-coded Credentials
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        if (("admin1".equals(user.getUsername()) && "adminpass1".equals(user.getPassword())) ||
                ("admin2".equals(user.getUsername()) && "adminpass2".equals(user.getPassword()))) {
            return "Welcome Admin " + user.getUsername() + "!";
        }

        User foundUser = userService.getUserByUsername(user.getUsername());
        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            return "Welcome " + foundUser.getUsername() + "!";
        }

        return "Invalid credentials!";
    }

    @GetMapping("/search")
    public User getUserByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username);
    }

    // OS Command Injection
    @PostMapping("/runCommand")
    public String runCommand(@RequestBody String command) {
        return userService.runCommand(command);
    }

    // Missing Authorization
    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Missing Authentication/Authorization
    @GetMapping("/{adminId}/messages")
    public ResponseEntity<?> getMessagesForAdmin(@PathVariable Long adminId) {
        List<Map<String, Object>> messages = userService.getMessagesForAdmin(adminId);

        if (messages.isEmpty()) {
            return ResponseEntity.ok("No messages found.");
        }

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/actions")
    public List<Map<String, Object>> getAllUserActions() {
        return userService.getAllUserActions();
    }
}

package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }


    @PostMapping("/send")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> sendMessageToSpecificAdmin(@RequestBody Map<String, String> payload) {
        try {
            Long senderId = Long.parseLong(payload.get("senderId"));
            Long adminId = Long.parseLong(payload.get("adminId"));
            String content = payload.get("content");

            String result = messageService.sendMessageToAdmin(senderId, adminId, content);
            return ResponseEntity.ok(result);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

}

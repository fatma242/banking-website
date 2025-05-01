package com.example.demo.controller;

import com.example.demo.model.Complaint;
import com.example.demo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;
    private final Path uploadDir = Paths.get(System.getProperty("user.home"), "Downloads", "banking website", "uploads");

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('USER')")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return complaintService.uploadFile(file);
    }

    @GetMapping("/view")
    public String viewFile(@RequestParam String filename) throws Exception {
        Path file = uploadDir.resolve(filename);
        if (Files.exists(file)) {
            return Files.readString(file);
        } else {
            return "File not found!";
        }
    }
}

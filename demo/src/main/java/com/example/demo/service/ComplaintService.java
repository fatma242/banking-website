package com.example.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.model.Complaint;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ComplaintService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Complaint> getAllComplaints() {
        String query = "SELECT c FROM Complaint c";
        return entityManager.createQuery(query, Complaint.class).getResultList();
    }

    public String uploadFile(MultipartFile file) {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (file.isEmpty()) {
            return "No file selected for upload!";
        }
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "Invalid file name.";
        }
        String[] allowedExtensions = { "txt", "pdf", "jpg", "jpeg", "png" };
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        boolean isAllowed = Arrays.asList(allowedExtensions).contains(fileExtension);
        if (!isAllowed) {
            return "File type not allowed.";
        }
        try {
            String safeFileName = Paths.get(originalFileName).getFileName().toString().replaceAll("[^a-zA-Z0-9\\.\\-_]",
                    "_");
            File destFile = new File(uploadDir + safeFileName);
            file.transferTo(destFile);
            Complaint complaint = new Complaint("Uploaded File", "File uploaded successfully",
                    destFile.getAbsolutePath());
            entityManager.persist(complaint);

            return "File uploaded successfully to: " + destFile.getAbsolutePath();
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }

}

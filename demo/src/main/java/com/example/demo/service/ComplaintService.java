package com.example.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.model.Complaint;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ComplaintService {

    @PersistenceContext
    private EntityManager entityManager;

    // Missing Authentication/Authorization
    public List<Complaint> getAllComplaints() {
        String query = "SELECT c FROM Complaint c";
        return entityManager.createQuery(query, Complaint.class).getResultList();
    }

    // Unrestricted File Upload
    public String uploadFile(MultipartFile file) {
        // Set a fixed path for uploads
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

        // Ensure the uploads directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }

        // Check if the file is empty
        if (file.isEmpty()) {
            return "No file selected for upload!";
        }

        try {
            // Create the destination files
            File destFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(destFile);
            // Save complaint to database
            Complaint complaint = new Complaint("Uploaded File", "File uploaded successfully",
                    destFile.getAbsolutePath());
            entityManager.persist(complaint); // Ensure complaint is saved
            return "File uploaded successfully to: " + destFile.getAbsolutePath();
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }

}

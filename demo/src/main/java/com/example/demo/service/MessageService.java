package com.example.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.model.Message;
import com.example.demo.model.User;

import java.util.List;

@Service
@Transactional
public class MessageService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Message> getAllMessages() {
        String query = "SELECT m FROM Message m";
        return entityManager.createQuery(query, Message.class).getResultList();
    }

    public String sendMessageToAdmin(Long senderId, Long adminId, String content) {
        User sender = getUserById(senderId);
        User admin  = getAdminById(adminId);

        // Sanitize the content by HTML-escaping
        String safeContent = HtmlUtils.htmlEscape(content);
        // Persist using the escaped text
        Message message = new Message(safeContent, sender, admin);
        entityManager.persist(message);
        return "Message Sent";
    }

    private User getUserById(Long userId) {
        String userQuery = "SELECT u FROM User u WHERE u.id = :userId AND u.role = 'USER'";
        try {
            return entityManager.createQuery(userQuery, User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("User with ID " + userId + " not found or is not a user.");
        }
    }
    
    private User getAdminById(Long adminId) {
        String adminQuery = "SELECT u FROM User u WHERE u.id = :adminId AND u.role = 'ADMIN'";
        try {
            return entityManager.createQuery(adminQuery, User.class)
                    .setParameter("adminId", adminId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Admin with ID " + adminId + " not found or is not an admin.");
        }
    }
    
}

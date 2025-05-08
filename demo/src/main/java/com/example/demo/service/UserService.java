package com.example.demo.service;

import com.example.demo.model.Message;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;


    public List<UserDTO> getAllUsers() {
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    public User getUserByUsername(String username) {
        String query = "SELECT u FROM User u WHERE u.username = :username";
        List<User> users = entityManager.createQuery(query, User.class)
                                        .setParameter("username", username)
                                        .getResultList();
    
        if (users.isEmpty()) {
            return null; // No user found
        }
        return users.get(0); // Return the first result (should only be one due to uniqueness)
    }

    public String runCommand(String input) {
        // Define internal mappings for allowed commands
        Map<String, String[]> commandMap = Map.of(
            "list", new String[] { "cmd.exe", "/c", "dir" },   // Windows
            "ping", new String[] { "cmd.exe", "/c", "ping 127.0.0.1" }
        );
        if (!commandMap.containsKey(input)) {
            return "Error: Command not allowed.";
        }
        try {
            Process process = new ProcessBuilder(commandMap.get(input)).start();
            process.waitFor();
            return "Command executed safely!";
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }
    

    public List<Map<String, Object>> getMessagesForAdmin(Long adminId) {
        String query = "SELECT m FROM Message m WHERE m.admin.id = :adminId";
        List<Message> messages = entityManager.createQuery(query, Message.class)
                .setParameter("adminId", adminId)
                .getResultList();

        List<Map<String, Object>> simplifiedMessages = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", message.getId());
            messageMap.put("senderUsername", message.getSender().getUsername());
            messageMap.put("content", message.getContent());
            simplifiedMessages.add(messageMap);
        }
        return simplifiedMessages;
    }

    public List<Map<String, Object>> getAllUserActions() {
        List<Map<String, Object>> actions = new ArrayList<>();

        // Timestamp Formatter
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm a");

        // Fetch Messages
        String messageQuery = "SELECT m FROM Message m";
        List<Message> messages = entityManager.createQuery(messageQuery, Message.class).getResultList();

        for (Message message : messages) {
            Map<String, Object> messageLog = new HashMap<>();
            messageLog.put("type", "Message");
            messageLog.put("userId", message.getSender().getId());
            messageLog.put("username", message.getSender().getUsername());
            messageLog.put("content", message.getContent());

            // Format message timestamp
            String formattedTimestamp = dateFormat.format(message.getTimestamp());
            messageLog.put("timestamp", formattedTimestamp);

            actions.add(messageLog);
        }

        // Fetch Transactions
        String transactionQuery = "SELECT t FROM Transaction t";
        List<Transaction> transactions = entityManager.createQuery(transactionQuery, Transaction.class).getResultList();

        for (Transaction transaction : transactions) {
            Map<String, Object> transactionLog = new HashMap<>();
            transactionLog.put("type", "Transaction");
            transactionLog.put("fromUserId", transaction.getFromAccount().getUser().getId());
            transactionLog.put("fromUsername", transaction.getFromAccount().getUser().getUsername());
            transactionLog.put("toUserId", transaction.getToAccount().getUser().getId());
            transactionLog.put("toUsername", transaction.getToAccount().getUser().getUsername());
            transactionLog.put("amount", transaction.getAmount());

            // Format transaction timestamp
            String formattedTimestamp = dateFormat.format(transaction.getDate());
            transactionLog.put("timestamp", formattedTimestamp);

            actions.add(transactionLog);
        }

        // Sort actions by timestamp (optional)
        actions.sort((a, b) -> ((String) b.get("timestamp")).compareTo((String) a.get("timestamp")));

        return actions;
    }

    public void saveUser(User foundUser) {
        entityManager.merge(foundUser);
    }
    

}

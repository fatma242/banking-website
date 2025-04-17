package com.example.demo.model;

import java.util.Date;


import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;  // XSS vulnerability
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id") 
    private User sender;

    @ManyToOne
    @JoinColumn(name = "admin_id")  
    private User admin;

    public Message() {
        this.timestamp = new Date(); 
    }

    public Message(String content, User sender, User admin) {
        this.content = content;
        this.sender = sender;
        this.admin = admin;
        this.timestamp = new Date(); 
    }

    // Getters & Setters
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }
}

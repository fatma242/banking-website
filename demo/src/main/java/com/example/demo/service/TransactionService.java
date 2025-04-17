package com.example.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.demo.model.Transaction;

import java.util.List;

@Service
@Transactional
public class TransactionService {

    @PersistenceContext
    private EntityManager entityManager;

    // Missing Authentication/Authorization 
    public List<Transaction> getAllTransactions() {
        String query = "SELECT t FROM Transaction t";
        return entityManager.createQuery(query, Transaction.class).getResultList();
    }
}



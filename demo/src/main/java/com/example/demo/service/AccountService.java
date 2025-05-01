package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AccountService {

    @PersistenceContext
    private EntityManager entityManager;

    // Missing Authentication
    public List<Account> getAllAccounts() {
        String query = "SELECT a FROM Account a";
        return entityManager.createQuery(query, Account.class).getResultList();
    }

    // SQL injection fixed
    public Account getAccountByNumber(String accountNumber) {
        try {
            String query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber";
            return entityManager.createQuery(query, Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null or handle the case appropriately
        }
    }

    // Integer Overflow
    public String transferMoney(Long fromAccountId, Long toAccountId, int amount) {
        Account fromAccount = entityManager.find(Account.class, fromAccountId);
        Account toAccount = entityManager.find(Account.class, toAccountId);

        if (fromAccount == null || toAccount == null) {
            return "One or both accounts not found.";
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        entityManager.merge(fromAccount);
        entityManager.merge(toAccount);

        Transaction transaction = new Transaction(fromAccount, toAccount, amount);
        entityManager.persist(transaction);
        entityManager.flush();

        return "Transfer successful!";
    }

    // Missing Authorization
    public Account createAccount(Account account) {
        entityManager.persist(account);
        return account;
    }
}

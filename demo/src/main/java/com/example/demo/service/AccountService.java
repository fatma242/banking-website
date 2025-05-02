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

    public List<Account> getAllAccounts() {
        String query = "SELECT a FROM Account a";
        return entityManager.createQuery(query, Account.class).getResultList();
    }

    public Account getAccountByNumber(String accountNumber) {
        String query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber";
        try {
            return entityManager.createQuery(query, Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    /*public List<Account> getAccountByNumber(String accountNumber) {       sql injection vulnerability
        String query = "SELECT a FROM Account a WHERE a.accountNumber = '" + accountNumber + "'";
        return entityManager.createQuery(query, Account.class).getResultList();
    }*/
    
    
    public String transferMoney(Long fromAccountId, Long toAccountId, int amount) {
        if (amount <= 0) {
            return "Transfer amount must be positive.";
        }
        Account fromAccount = entityManager.find(Account.class, fromAccountId);
        Account toAccount = entityManager.find(Account.class, toAccountId);
        if (fromAccount == null || toAccount == null) {
            return "One or both accounts not found.";
        }
        int fromBalance = fromAccount.getBalance();
        int toBalance = toAccount.getBalance();
        if (fromBalance < amount) {
            return "Insufficient funds in the sender's account.";
        }
        if ((long) toBalance + amount > Integer.MAX_VALUE) {
            return "Transfer would cause overflow in the recipient's account.";
        }
        fromAccount.setBalance(fromBalance - amount);
        toAccount.setBalance(toBalance + amount);
        entityManager.merge(fromAccount);
        entityManager.merge(toAccount);
        Transaction transaction = new Transaction(fromAccount, toAccount, amount);
        entityManager.persist(transaction);
        entityManager.flush();
        return "Transfer successful!";
    }
    

    public Account createAccount(Account account) {
        entityManager.persist(account);
        return account;
    }
}

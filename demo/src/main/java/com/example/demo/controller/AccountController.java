package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Missing Authentication
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // SQL Injection
    @GetMapping("/{accountNumber}")
    public Account getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber);
    }

    // Integer Overflow
    @PostMapping("/transfer")

    public String transferMoney(@RequestBody Transaction transaction) {
        try {
            Long fromAccountId = transaction.getFromAccount().getId();
            Long toAccountId = transaction.getToAccount().getId();
            int amount =  (int) transaction.getAmount();  
            return accountService.transferMoney(fromAccountId, toAccountId, amount);
        } catch (NumberFormatException e) {
            return "Invalid account IDs!";
        }
    }
    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

}

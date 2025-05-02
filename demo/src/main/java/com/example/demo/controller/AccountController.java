package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(account);
    }
    /*@GetMapping("/{accountNumber}")                       sql injection vulnerability
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Account>> getAccountByNumber(@PathVariable String accountNumber) {
        List<Account> account = accountService.getAccountByNumber(accountNumber);

        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(account);
    }*/

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('USER')")
    public String transferMoney(@RequestBody Transaction transaction) {
        try {
            Long fromAccountId = transaction.getFromAccount().getId();
            Long toAccountId = transaction.getToAccount().getId();
            int amount = (int) transaction.getAmount();
            return accountService.transferMoney(fromAccountId, toAccountId, amount);
        } catch (NumberFormatException e) {
            return "Invalid account IDs!";
        }
    }

}

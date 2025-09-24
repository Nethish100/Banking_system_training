package com.securebank.controller;

import com.securebank.dto.AccountDto;
import com.securebank.entity.Account;
import com.securebank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        try {
            List<AccountDto> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{accountNo}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNo) {
        try {
            AccountDto account = accountService.getAccountByNumber(accountNo);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving account: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountDto accountDto) {
        try {
            AccountDto savedAccount = accountService.createAccount(accountDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating account: " + e.getMessage());
        }
    }

    @DeleteMapping("/{accountNo}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountNo) {
        try {
            accountService.deleteAccount(accountNo);
            return ResponseEntity.ok().body("Account deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting account: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAccountCount() {
        try {
            long count = accountService.getAccountCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    @GetMapping("/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance() {
        try {
            BigDecimal totalBalance = accountService.getTotalBalance();
            return ResponseEntity.ok(totalBalance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BigDecimal.ZERO);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDto>> getAccountsByCustomerId(@PathVariable Long customerId) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/type/{accountType}")
    public ResponseEntity<List<AccountDto>> getAccountsByType(@PathVariable Account.AccountType accountType) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByType(accountType);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
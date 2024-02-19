package com.futurebank.accountService.controller;

import com.futurebank.accountService.model.Account;
import com.futurebank.accountService.model.AccountCreationRequest;
import com.futurebank.accountService.model.Transaction;
import com.futurebank.accountService.model.TransferRequest;
import com.futurebank.accountService.service.AccountService;
import com.futurebank.accountService.service.TransactionService;
import com.futurebank.accountService.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*") // Adjust the origins as per your requirements
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransferService transferService;

    public AccountController(AccountService accountService, TransactionService transactionService, TransferService transferService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountCreationRequest request) {
        try {
            Account account = accountService.createAccount(request.getUserId(), request.getAccountType());
            return new ResponseEntity<>(account, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Account creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<?> getAccountBalance(@PathVariable Long accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                return ResponseEntity.ok(account.getBalance());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching account balance: " + e.getMessage());
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody Account accountDetails) {
        try {
            Account updatedAccount = accountService.updateAccount(accountId, accountDetails);
            if (updatedAccount != null) {
                return ResponseEntity.ok(updatedAccount);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating account: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        if (!accounts.isEmpty()) {
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        try {
            boolean isDeleted = accountService.deleteAccount(accountId);
            if (isDeleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> transferFunds(@RequestBody TransferRequest transferRequest) {
        try {
            Transaction transaction = transferService.transferFunds(
                    transferRequest.getFromAccount(),
                    transferRequest.getToAccount(),
                    transferRequest.getAmount(),
                    transferRequest.getCategory());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during fund transfer: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<?> getTransactionsByAccountId(
            @PathVariable Long accountId,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<Integer> month,
            @RequestParam Optional<String> startDate,
            @RequestParam Optional<String> endDate) {

        try {
            List<Transaction> transactions = transactionService.determineTransactionQuery(accountId, year, month, startDate, endDate);
            if (!transactions.isEmpty()) {
                return ResponseEntity.ok(transactions);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching transactions: " + e.getMessage());
        }
    }
}

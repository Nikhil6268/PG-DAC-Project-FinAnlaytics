package com.futurebank.accountService.service;

import com.futurebank.accountService.model.Account;
import com.futurebank.accountService.model.MyTransactionCategory;
import com.futurebank.accountService.model.Transaction;
import com.futurebank.accountService.repository.AccountRepository;
import com.futurebank.accountService.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount, MyTransactionCategory category) {
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new IllegalArgumentException("From Account not found with ID: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new IllegalArgumentException("To Account not found with ID: " + toAccountId));
        
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCategory(category);
        
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByCategory(MyTransactionCategory category) {
        return transactionRepository.findByCategory(category);
    }

    public List<Transaction> getTransactionHistoryByAccountId(Long accountId) {
        return accountRepository.findById(accountId)
                .map(account -> transactionRepository.findByAccount(account))
                .orElse(Collections.emptyList()); // Return an empty list if account not found
    }

    public List<Transaction> getTransactionHistory(Long accountId, Optional<Integer> year, Optional<Integer> month,
                                                   Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if (year.isPresent() && month.isPresent()) {
            LocalDate startOfMonth = YearMonth.of(year.get(), month.get()).atDay(1);
            LocalDate endOfMonth = YearMonth.of(year.get(), month.get()).atEndOfMonth();
            return transactionRepository.findByAccountAndTransactionDateBetween(account, startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
        } else if (startDate.isPresent() && endDate.isPresent()) {
            return transactionRepository.findByAccountAndTransactionDateBetween(account, startDate.get().atStartOfDay(), endDate.get().atTime(23, 59, 59));
        } else {
            return transactionRepository.findByAccount(account);
        }
    }

    public List<Transaction> determineTransactionQuery(Long accountId, Optional<Integer> year, Optional<Integer> month,
            Optional<String> startDate, Optional<String> endDate) {
Account account = accountRepository.findById(accountId)
.orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

// Prepare DateTimeFormatter for parsing dates
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

if (year.isPresent() && month.isPresent()) {
LocalDate startOfMonth = LocalDate.of(year.get(), month.get(), 1);
LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
return transactionRepository.findByAccountAndTransactionDateBetween(account, startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
} else if (startDate.isPresent() && endDate.isPresent()) {
LocalDate start = LocalDate.parse(startDate.get(), formatter);
LocalDate end = LocalDate.parse(endDate.get(), formatter);
return transactionRepository.findByAccountAndTransactionDateBetween(account, start.atStartOfDay(), end.atTime(23, 59, 59));
} else {
// If no filters are provided, return all transactions for the account
return transactionRepository.findByAccount(account);
}
}
}

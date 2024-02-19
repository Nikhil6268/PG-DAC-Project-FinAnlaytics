package com.futurebank.accountService.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futurebank.accountService.model.MyTransactionCategory;
import com.futurebank.accountService.model.Transaction;
import com.futurebank.accountService.model.Account;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Use JpaRepository for full CRUD functionality and paging support

    // Fetch transactions involving a specific account, either as sender or receiver
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account")
    List<Transaction> findByAccount(@Param("account") Account account);

    // Fetch transactions by category
    List<Transaction> findByCategory(MyTransactionCategory category);

    // Fetch transactions within a date range and by category
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end AND t.category = :category")
    List<Transaction> findByTransactionDateBetweenAndCategory(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end, 
        @Param("category") MyTransactionCategory category
    );

    // Fetch transactions within a date range
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findByTransactionDateBetween(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );

    // Fetch transactions for an account within a date range
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :account OR t.toAccount = :account) AND t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findByAccountAndTransactionDateBetween(
        @Param("account") Account account, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );

    // The following method has been adjusted for the updated model. Assuming you've updated the Transaction class accordingly.
    // For example, if you added associations in Transaction (e.g., @ManyToOne to Account for fromAccount and toAccount),
    // ensure these are reflected in your queries.

    // Additional Queries can be added as needed
}

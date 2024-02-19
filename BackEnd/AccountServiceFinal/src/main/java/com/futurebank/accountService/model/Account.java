package com.futurebank.accountService.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts") // Specify table name for clarity
@Getter // Lombok annotation to generate all getters
@Setter // Use with caution; direct manipulation of some fields should be restricted
@NoArgsConstructor // Simplifies the creation of an Account without the need to manually define a no-arg constructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNumber; // Unique identifier for the account

    @NotNull
    @PositiveOrZero
    private BigDecimal balance = BigDecimal.ZERO; // Initialize balance to a default value directly

    private String accountType; // Type of account (e.g., savings, checking)

    private Long userId; // Associated user identifier

    // Custom constructor for creating an account with a specific user, type, and initial balance
    public Account(Long userId, String accountType, BigDecimal initialBalance) {
        this.userId = userId;
        this.accountType = accountType;
        this.balance = initialBalance.compareTo(BigDecimal.ZERO) >= 0 ? initialBalance : BigDecimal.ZERO;
    }

    // Method to deposit amount to account
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) { // Check if amount is positive
            this.balance = this.balance.add(amount);
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    // Method to withdraw amount from account
    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && this.balance.compareTo(amount) >= 0) { // Check if amount is positive and sufficient funds are available
            this.balance = this.balance.subtract(amount);
            return true; // Indicates successful withdrawal
        } else {
            return false; // Indicates withdrawal was not successful due to negative amount or insufficient funds
        }
    }

    // Consider removing the direct public setter for balance to prevent misuse
    // Instead, manage balance changes through deposit and withdraw methods
}

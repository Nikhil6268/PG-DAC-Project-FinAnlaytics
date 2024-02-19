package com.futurebank.accountService.model;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
@Entity
@Table(name = "transactions") // Specify table name for clarity
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    // Assuming Account is another entity class that represents the account details
    @ManyToOne
    @JoinColumn(name = "fromAccountId", nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "toAccountId", nullable = false)
    private Account toAccount;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 19, scale = 4) // Define precision and scale for financial amount
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50) // Specify length according to the longest enum value
    private MyTransactionCategory category;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    public Transaction(Account fromAccount, Account toAccount, BigDecimal amount, MyTransactionCategory category) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.category = category;
        // transactionDate is set automatically by @CreationTimestamp
    }

	public void setAccount(Account account) {
		// TODO Auto-generated method stub
		
	}

    // Note: Lombok @Getter, @Setter, and @NoArgsConstructor annotations are utilized, so explicit getters, setters, and a no-args constructor are not manually defined.
}

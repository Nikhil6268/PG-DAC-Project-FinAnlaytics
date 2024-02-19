package com.futurebank.accountService.DTO;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.futurebank.accountService.model.MyTransactionCategory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MonthlyExpenditureDTO {

    private MyTransactionCategory category; // Assuming you want to use the Enum here.
    private BigDecimal totalAmount;
    private YearMonth month;

    // Updated constructor
    public MonthlyExpenditureDTO(MyTransactionCategory category, BigDecimal totalAmount, YearMonth month) {
        this.category = category;
        // Ensures totalAmount is never null to avoid nullPointerException in operations
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.month = month;
    }

    // Ensure totalAmount is never returned as null to avoid NullPointerException
    public BigDecimal getTotalAmount() {
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    // Additional constructor for convenience, can be used when category or month might not be known
    public MonthlyExpenditureDTO() {
        // Initialize totalAmount to BigDecimal.ZERO to avoid nulls in arithmetic operations
        this.totalAmount = BigDecimal.ZERO;
    }

    // If you need to update totalAmount and ensure it's never null
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
}

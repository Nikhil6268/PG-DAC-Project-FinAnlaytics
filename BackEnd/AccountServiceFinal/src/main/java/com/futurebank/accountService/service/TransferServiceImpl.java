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
import java.time.LocalDateTime;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount, MyTransactionCategory category) {
        validateTransferAmount(amount);

        Account fromAccount = findAccountById(fromAccountId, "From");
        Account toAccount = findAccountById(toAccountId, "To");
        validateSufficientFunds(fromAccount, amount);

        updateAccountBalances(fromAccount, toAccount, amount);

        return createAndSaveTransaction(fromAccount, toAccount, amount, category);
    }

    @Override
    public Transaction transferFunds(Long fromAccountId, Long toAccountId, Double amount, String category) {
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        MyTransactionCategory transactionCategory;
        try {
            transactionCategory = MyTransactionCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction category: " + category);
        }
        return transferFunds(fromAccountId, toAccountId, transferAmount, transactionCategory);
    }

    private void validateTransferAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
    }

    private Account findAccountById(Long accountId, String accountType) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(accountType + " account not found with ID: " + accountId));
    }

    private void validateSufficientFunds(Account fromAccount, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the from account");
        }
    }

    private void updateAccountBalances(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        // No need to explicitly save the accounts here due to the @Transactional annotation
        // and assuming the entities are managed and will be automatically persisted at the end of the transaction
    }

    private Transaction createAndSaveTransaction(Account fromAccount, Account toAccount, BigDecimal amount, MyTransactionCategory category) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}

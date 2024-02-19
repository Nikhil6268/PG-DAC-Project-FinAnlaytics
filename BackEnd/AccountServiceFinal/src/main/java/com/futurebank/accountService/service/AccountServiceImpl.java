package com.futurebank.accountService.service;

import com.futurebank.accountService.model.Account;
import com.futurebank.accountService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(Long userId, String accountType) {
    	System.out.println("cREATING ACCOUNT OBJECT :");
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.valueOf(10000)); // Initialize with zero balance
        // Assuming your Account entity has a method to set the account number
        // and you want to generate a unique account number for each account
        account.setAccountNumber(generateUniqueAccountNumber());
        accountRepository.save(account);
        System.out.println("sAVING  ACCOUNT OBJECT :"+account.toString());
        return account;
    }
  
    private Long generateUniqueAccountNumber() {
        Long accountNumber;
        do {
            accountNumber = System.currentTimeMillis(); // This method of generating unique account numbers is simplistic and might not be suitable for all applications
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Override
    public Account updateAccount(Long accountId, Account accountDetails) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found for this id :: " + accountId));
        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setBalance(accountDetails.getBalance());
        account.setAccountType(accountDetails.getAccountType());
        return accountRepository.save(account);
    }

    
    public BigDecimal getAccountBalance(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            return accountOpt.get().getBalance();
        } else {
            throw new RuntimeException("Account not found for this id :: " + accountId);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.orElseThrow(() -> new RuntimeException("Account not found for this id :: " + accountId));
    }

    @Override
    public boolean deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found for this id :: " + accountId));
        accountRepository.delete(account);
        return true;
    }
}

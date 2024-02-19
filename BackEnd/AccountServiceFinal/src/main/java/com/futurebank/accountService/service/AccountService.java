package com.futurebank.accountService.service;

import com.futurebank.accountService.model.Account;
import java.util.List;

public interface AccountService {
   
    Account updateAccount(Long accountId, Account accountDetails);
    List<Account> getAllAccounts();
    Account getAccountById(Long accountId);
    boolean deleteAccount(Long accountId);
	Account createAccount(Long userId, String accountType);
}

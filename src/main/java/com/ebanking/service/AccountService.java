package com.ebanking.service;

import com.ebanking.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByNumber(String accountNumber);
    List<Account> getAllAccounts();
    List<Account> getAccountsByUserId(Long userId);
    boolean deleteAccount(Long accountId);
    boolean isAccountNumberExists(String accountNumber);
    String generateAccountNumber();
    Optional<Account> updateAccount(Account account);

}
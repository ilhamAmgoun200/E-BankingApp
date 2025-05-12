package com.ebanking.repository;

import com.ebanking.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(Long id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByUserId(Long userId);
    void delete(Account account);
    boolean existsByAccountNumber(String accountNumber);
}
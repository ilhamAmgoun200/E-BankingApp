package com.ebanking.service;

import com.ebanking.model.Account;
import com.ebanking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account createAccount(Account account) {
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            account.setAccountNumber(generateAccountNumber());
        }

        if (account.getCreationDate() == null) {
            account.setCreationDate(LocalDateTime.now());
        }

        account.setLastUpdated(LocalDateTime.now());

        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long accountId) {
        System.out.println("Service: Getting account by ID: " + accountId);
        try {
            return accountRepository.findById(accountId);
        } catch (Exception e) {
            System.err.println("Error in getAccountById: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) {
        System.out.println("Service: Getting account by number: " + accountNumber);
        try {
            return accountRepository.findByAccountNumber(accountNumber);
        } catch (Exception e) {
            System.err.println("Error in getAccountByNumber: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional // Assurez-vous que cette annotation est présente
    public List<Account> getAllAccounts() {
        try {
            System.out.println("AccountService: Getting all accounts...");
            List<Account> accounts = accountRepository.findAll();
            System.out.println("AccountService: Found " + accounts.size() + " accounts");
            return accounts;
        } catch (Exception e) {
            System.err.println("Error in AccountService.getAllAccounts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        System.out.println("Service: Getting accounts by user ID: " + userId);
        try {
            return accountRepository.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error in getAccountsByUserId: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    @Override
    @Transactional
    public boolean deleteAccount(Long accountId) {
        System.out.println("Service: Deleting account with ID: " + accountId);
        try {
            return accountRepository.findById(accountId)
                    .map(account -> {
                        accountRepository.delete(account);
                        return true;
                    })
                    .orElse(false);
        } catch (Exception e) {
            System.err.println("Error in deleteAccount: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountNumberExists(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            // Générer un numéro de compte de 10 chiffres
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            accountNumber = sb.toString();
        } while (isAccountNumberExists(accountNumber));

        return accountNumber;
    }


    @Override
    @Transactional
    public Optional<Account> updateAccount(Account account) {
        System.out.println("Service: Updating account with ID: " + account.getAccountId());
        try {
            // Vérifier si le compte existe
            System.out.println("Checking if account exists...");
            Optional<Account> existingAccountOpt = accountRepository.findById(account.getAccountId());
            if (!existingAccountOpt.isPresent()) {
                System.out.println("Account not found with ID: " + account.getAccountId());
                return Optional.empty();
            }

            // Récupérer le compte existant
            Account existingAccount = existingAccountOpt.get();

            // Mettre à jour uniquement les champs non null de la requête
            if (account.getAccountNumber() != null) {
                existingAccount.setAccountNumber(account.getAccountNumber());
            }
            if (account.getAccountType() != null) {
                existingAccount.setAccountType(account.getAccountType());
            }
            if (account.getBalance() != null) {
                existingAccount.setBalance(account.getBalance());
            }
            if (account.getCurrencyCode() != null) {
                existingAccount.setCurrencyCode(account.getCurrencyCode());
            }
            if (account.getStatus() != null) {
                existingAccount.setStatus(account.getStatus());
            }
            if (account.getInterestRate() != null) {
                existingAccount.setInterestRate(account.getInterestRate());
            }
            if (account.getOverdraftLimit() != null) {
                existingAccount.setOverdraftLimit(account.getOverdraftLimit());
            }
            if (account.getUserId() != null) {
                existingAccount.setUserId(account.getUserId());
            }
            if (account.getBranchId() != null) {
                existingAccount.setBranchId(account.getBranchId());
            }

            // Mettre à jour la date de dernière modification
            existingAccount.setLastUpdated(LocalDateTime.now());

            System.out.println("Saving account...");
            Account updatedAccount = accountRepository.save(existingAccount);
            System.out.println("Account updated successfully");
            return Optional.of(updatedAccount);
        } catch (Exception e) {
            System.err.println("Error in updateAccount: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}
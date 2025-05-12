package com.ebanking.controller;


import com.ebanking.model.Account;
import com.ebanking.service.AccountService;
import com.ebanking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            System.out.println("Received account: " + account);
            System.out.println("Account type: " + account.getAccountType());
            System.out.println("Balance: " + account.getBalance());
            System.out.println("Currency: " + account.getCurrencyCode());
            System.out.println("User ID: " + account.getUserId());

            Account createdAccount = accountService.createAccount(account);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            System.out.println("AccountController: Getting all accounts...");
            List<Account> accounts = accountService.getAllAccounts();
            System.out.println("AccountController: Found " + accounts.size() + " accounts");

            // Vérifiez chaque compte pour détecter d'éventuels problèmes
            for (Account account : accounts) {
                System.out.println("Account ID: " + account.getAccountId() +
                        ", Number: " + account.getAccountNumber() +
                        ", Type: " + account.getAccountType() +
                        ", Balance: " + account.getBalance() +
                        ", Currency: " + account.getCurrencyCode() +
                        ", Status: " + account.getStatus() +
                        ", Creation Date: " + account.getCreationDate() +
                        ", Last Updated: " + account.getLastUpdated());
            }

            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in getAllAccounts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") Long accountId) {
        System.out.println("Searching for account with ID: " + accountId);

        try {
            return accountService.getAccountById(accountId)
                    .map(account -> {
                        System.out.println("Account found: " + account.getAccountId());
                        return new ResponseEntity<>(account, HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("Error retrieving account by ID: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable("accountNumber") String accountNumber) {
        // Ajoutez des logs pour déboguer
        System.out.println("Searching for account with number: " + accountNumber);

        try {
            return accountService.getAccountByNumber(accountNumber)
                    .map(account -> {
                        System.out.println("Account found: " + account.getAccountId());
                        return new ResponseEntity<>(account, HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("Error retrieving account by number: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Récupérer tous les comptes d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountsByUserId(@PathVariable("userId") Long userId) {
        try {
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") Long accountId) {
        System.out.println("Deleting account with ID: " + accountId);

        try {
            if (accountService.deleteAccount(accountId)) {
                System.out.println("Account deleted successfully");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                System.out.println("Account not found for deletion");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable("accountId") Long accountId,
            @RequestBody Account account) {
        System.out.println("Updating account with ID: " + accountId);
        try {
            account.setAccountId(accountId); // Ensure ID is set correctly
            return accountService.updateAccount(account)
                    .map(updatedAccount -> {
                        System.out.println("Account updated successfully");
                        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("Error updating account: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer un compte pour un utilisateur spécifique
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAccountForUser(
            @PathVariable("userId") Long userId,
            @RequestBody Account accountDetails) {

        try {
            // Vérifier si l'utilisateur existe
            if (!userService.existsById(userId)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Définir l'ID utilisateur dans le compte
            accountDetails.setUserId(userId);

            // Créer le compte
            Account createdAccount = accountService.createAccount(accountDetails);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}

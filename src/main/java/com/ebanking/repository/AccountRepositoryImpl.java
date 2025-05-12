package com.ebanking.repository;

import com.ebanking.model.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        try {
            return sessionFactory.getCurrentSession();
        } catch (Exception e) {
            System.err.println("Error getting current session: " + e.getMessage());
            return sessionFactory.openSession();
        }
    }

    @Override
    public Account save(Account account) {
        try {
            System.out.println("Repository: Saving account with ID: " + account.getAccountId());
            Session session = getCurrentSession();
            session.merge(account); // Utilisez merge au lieu de saveOrUpdate pour Hibernate 6
            System.out.println("Repository: Account saved successfully");
            return account;
        } catch (Exception e) {
            System.err.println("Error saving account: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    @Override
    public Optional<Account> findById(Long id) {
        Account account = getCurrentSession().get(Account.class, id);
        return Optional.ofNullable(account);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            System.out.println("Repository: Finding account by number: " + accountNumber);
            Query<Account> query = getCurrentSession().createQuery(
                    "FROM Account WHERE accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            System.err.println("Error finding account by number: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public List<Account> findAll() {
        try {
            System.out.println("Fetching all accounts...");
            List<Account> accounts = getCurrentSession().createQuery("FROM Account", Account.class).list();
            System.out.println("Found " + accounts.size() + " accounts");
            return accounts;
        } catch (Exception e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public List<Account> findByUserId(Long userId) {
        Query<Account> query = getCurrentSession().createQuery(
                "FROM Account WHERE userId = :userId", Account.class);
        query.setParameter("userId", userId);
        return query.list();
    }

    @Override
    public void delete(Account account) {
        getCurrentSession().remove(account);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        Query<Long> query = getCurrentSession().createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.accountNumber = :accountNumber", Long.class);
        query.setParameter("accountNumber", accountNumber);
        return query.uniqueResult() > 0;
    }
}
package com.ebanking.repository;

import com.ebanking.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);

}

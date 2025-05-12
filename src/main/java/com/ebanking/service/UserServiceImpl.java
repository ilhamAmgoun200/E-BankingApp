package com.ebanking.service;

import com.ebanking.model.User;
import com.ebanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        try {
            // Vérifier si l'email existe déjà
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Définir la date de création
            user.setCreationDate(LocalDateTime.now());

            // Définir le statut par défaut si non spécifié
            if (user.getStatus() == null) {
                user.setStatus("ACTIVE");
            }

            // Définir le rôle par défaut si non spécifié
            if (user.getRole() == null) {
                user.setRole("CLIENT");
            }

            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            // Vérifier si l'email est modifié et s'il existe déjà
            if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                    userRepository.existsByEmail(userDetails.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Mettre à jour les champs
            if (userDetails.getFirstName() != null) {
                existingUser.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                existingUser.setLastName(userDetails.getLastName());
            }
            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPassword() != null) {
                existingUser.setPassword(userDetails.getPassword());
            }
            if (userDetails.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            }
            if (userDetails.getAddress() != null) {
                existingUser.setAddress(userDetails.getAddress());
            }
            if (userDetails.getStatus() != null) {
                existingUser.setStatus(userDetails.getStatus());
            }
            if (userDetails.getRole() != null) {
                existingUser.setRole(userDetails.getRole());
            }

            return userRepository.save(existingUser);
        });
    }
    @Override
    public boolean existsById(Long id) {
        try {
            return userRepository.existsById(id);
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Optional<User> updateLastLogin(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        });
    }
}

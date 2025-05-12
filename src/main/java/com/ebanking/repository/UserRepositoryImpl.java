package com.ebanking.repository;

import com.ebanking.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAddress(rs.getString("address"));

        Timestamp creationDate = rs.getTimestamp("creation_date");
        if (creationDate != null) {
            user.setCreationDate(creationDate.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        user.setStatus(rs.getString("status"));
        user.setRole(rs.getString("role"));

        return user;
    };

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";

            List<User> users = jdbcTemplate.query(sql, userRowMapper, id);

            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé avec ID: " + id);
                return Optional.empty();
            } else {
                return Optional.of(users.get(0));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur par ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    @Override
    public Optional<User> findByEmail(String email) {
        try {
            String sql = "SELECT * FROM users WHERE email = ?";
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByRole(String role) {
        String sql = "SELECT * FROM users WHERE role = ?";
        return jdbcTemplate.query(sql, userRowMapper, role);
    }

    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            // Insert new user
            return insertUser(user);
        } else {
            // Update existing user
            return updateUser(user);
        }
    }

    private User insertUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, phone_number, address, creation_date, status, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id";

        Long userId = jdbcTemplate.queryForObject(sql, Long.class,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                user.getAddress() != null ? user.getAddress() : "",
                Timestamp.valueOf(user.getCreationDate() != null ? user.getCreationDate() : LocalDateTime.now()),
                user.getStatus() != null ? user.getStatus() : "ACTIVE",
                user.getRole() != null ? user.getRole() : "CLIENT"
        );

        user.setUserId(userId);
        return user;
    }


    // Dans UserRepositoryImpl
    private User updateUser(User user) {
        try {
            String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, " +
                    "phone_number = ?, address = ?, status = ?, role = ? " +
                    "WHERE user_id = ?";

            int rowsAffected = jdbcTemplate.update(sql,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    user.getAddress() != null ? user.getAddress() : "",
                    user.getStatus() != null ? user.getStatus() : "ACTIVE",
                    user.getRole() != null ? user.getRole() : "CLIENT",
                    user.getUserId());

            if (rowsAffected == 0) {
                System.out.println("Aucune ligne mise à jour pour l'utilisateur avec ID: " + user.getUserId());
            } else {
                System.out.println("Utilisateur mis à jour avec succès, ID: " + user.getUserId());
            }

            return user;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void deleteById(Long id) {
        try {
            String sql = "DELETE FROM users WHERE user_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, id);

            if (rowsAffected == 0) {
                System.out.println("Aucun utilisateur trouvé à supprimer avec ID: " + id);
            } else {
                System.out.println("Utilisateur supprimé avec succès, ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public boolean existsById(Long id) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean existsByEmail(String email) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'utilisateur par email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}

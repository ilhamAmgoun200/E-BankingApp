package com.ebanking.controller;

import com.ebanking.model.User;
import com.ebanking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            System.out.println("Recherche de l'utilisateur avec ID: " + id);

            Optional<User> userOpt = userService.getUserById(id);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Utilisateur trouvé: " + user);
                return ResponseEntity.ok(user);
            } else {
                System.out.println("Utilisateur non trouvé avec ID: " + id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            System.out.println("Tentative de création d'utilisateur : " + user.toString());
            User createdUser = userService.createUser(user);
            System.out.println("Utilisateur créé avec succès : " + createdUser.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User userDetails) {
        try {
            System.out.println("Tentative de mise à jour de l'utilisateur avec ID: " + id);
            System.out.println("Détails de la mise à jour: " + userDetails);

            return userService.updateUser(id, userDetails)
                    .map(updatedUser -> {
                        System.out.println("Utilisateur mis à jour avec succès: " + updatedUser);
                        return ResponseEntity.ok(updatedUser);
                    })
                    .orElseGet(() -> {
                        System.out.println("Utilisateur non trouvé avec ID: " + id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de validation: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") Long id) {
        try {
            System.out.println("Tentative de suppression de l'utilisateur avec ID: " + id);

            boolean deleted = userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();

            if (deleted) {
                System.out.println("Utilisateur supprimé avec succès, ID: " + id);
                response.put("message", "User deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Utilisateur non trouvé avec ID: " + id);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/{id}/login")
    public ResponseEntity<?> updateLastLogin(@PathVariable Long id) {
        return userService.updateLastLogin(id)
                .map(user -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Last login updated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

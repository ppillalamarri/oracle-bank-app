package com.revature.bank.controller;

import com.revature.bank.model.User;
import com.revature.bank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for all banking operations.
 * Replaces the console-based BankDriver with HTTP endpoints.
 */
@RestController
@RequestMapping("/api")
public class BankController {

    private final UserService userService;

    public BankController(UserService userService) {
        this.userService = userService;
    }

    // ==================== User Endpoints ====================

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> credentials) {
        User user = userService.login(credentials.get("username"), credentials.get("password"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Map<String, String> body) {
        User user = userService.registerUser(
                body.get("firstname"), body.get("lastname"),
                body.get("username"), body.get("password"),
                body.get("email"), body.get("phone"),
                body.get("street"), body.get("city"),
                body.get("state"), body.get("zip"),
                body.get("accountType"));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllCustomers() {
        return ResponseEntity.ok(userService.getAllCustomers());
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Account Endpoints ====================

    @PostMapping("/users/{userId}/accounts")
    public ResponseEntity<User> addAccount(@PathVariable String userId,
                                           @RequestBody Map<String, String> body) {
        User user = userService.addAccount(userId, body.get("accountType"));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/accounts/pending")
    public ResponseEntity<List<User>> getPendingAccounts() {
        return ResponseEntity.ok(userService.getUsersWithPendingAccounts());
    }

    @PutMapping("/users/{userId}/accounts/{accountId}/approve")
    public ResponseEntity<User> approveAccount(@PathVariable String userId,
                                               @PathVariable String accountId) {
        return ResponseEntity.ok(userService.approveAccount(userId, accountId));
    }

    @PutMapping("/users/{userId}/accounts/{accountId}/deny")
    public ResponseEntity<?> denyAccount(@PathVariable String userId,
                                         @PathVariable String accountId) {
        User user = userService.denyAccount(userId, accountId);
        if (user == null) {
            return ResponseEntity.ok(Map.of("message", "Account denied and user deleted"));
        }
        return ResponseEntity.ok(user);
    }

    // ==================== Transaction Endpoints ====================

    @PostMapping("/users/{userId}/accounts/{accountId}/deposit")
    public ResponseEntity<User> deposit(@PathVariable String userId,
                                        @PathVariable String accountId,
                                        @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.deposit(userId, accountId, body.get("amount")));
    }

    @PostMapping("/users/{userId}/accounts/{accountId}/withdraw")
    public ResponseEntity<User> withdraw(@PathVariable String userId,
                                         @PathVariable String accountId,
                                         @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.withdraw(userId, accountId, body.get("amount")));
    }

    @PostMapping("/users/{userId}/accounts/{accountId}/transfer")
    public ResponseEntity<Map<String, String>> transfer(@PathVariable String userId,
                                                        @PathVariable String accountId,
                                                        @RequestBody Map<String, String> body) {
        userService.transfer(userId, accountId, body.get("toAccountId"),
                Double.parseDouble(body.get("amount")));
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }
}

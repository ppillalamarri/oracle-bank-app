package com.revature.bank.service;

import com.revature.bank.exception.DuplicateAccountException;
import com.revature.bank.exception.InsufficientFundsException;
import com.revature.bank.exception.ResourceNotFoundException;
import com.revature.bank.model.Account;
import com.revature.bank.model.User;
import com.revature.bank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for banking operations.
 * Replaces the business logic from BankDriver, UserFake, and AccountFake.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ==================== User Operations ====================

    public User login(String username, String password) {
        log.debug("Login attempt for username: {}", username);
        return userRepository.findByUsernameAndPassword(username, password)
                .orElse(null);
    }

    public User registerUser(String firstname, String lastname, String username,
                             String password, String email, String phone,
                             String street, String city, String state, String zip,
                             String accountType) {
        log.debug("Registering new user: {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateAccountException("Username '" + username + "' already exists");
        }

        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setType("customer");

        com.revature.bank.model.Address address = new com.revature.bank.model.Address(street, city, state, zip);
        user.setAddress(address);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(0.01);
        account.setType(accountType);
        account.setApproved("pending");
        account.setJoint(0);

        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);

        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public List<User> getAllCustomers() {
        return userRepository.findByType("customer");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String userId) {
        log.debug("Deleting user: {}", userId);
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    // ==================== Account Operations ====================

    public User addAccount(String userId, String accountType) {
        log.debug("Adding {} account for user: {}", accountType, userId);
        User user = getUserById(userId);

        boolean hasCheckings = user.getAccounts().stream()
                .anyMatch(a -> "checkings".equals(a.getType()));
        boolean hasSavings = user.getAccounts().stream()
                .anyMatch(a -> "savings".equals(a.getType()));

        if (hasCheckings && hasSavings) {
            throw new DuplicateAccountException("User already has both checkings and savings accounts");
        }

        if ("checkings".equals(accountType) && hasCheckings) {
            throw new DuplicateAccountException("User already has a checkings account");
        }
        if ("savings".equals(accountType) && hasSavings) {
            throw new DuplicateAccountException("User already has a savings account");
        }

        // If primary account is approved, auto-approve secondary
        String approvalStatus = "pending";
        if (!user.getAccounts().isEmpty()
                && "accepted".equals(user.getAccounts().get(0).getApproved())) {
            approvalStatus = "accepted";
        }

        Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(0.01);
        account.setType(accountType);
        account.setApproved(approvalStatus);
        account.setJoint(0);

        user.getAccounts().add(account);
        return userRepository.save(user);
    }

    public List<User> getUsersWithPendingAccounts() {
        return userRepository.findUsersWithPendingAccounts();
    }

    public User approveAccount(String userId, String accountId) {
        log.debug("Approving account {} for user {}", accountId, userId);
        User user = getUserById(userId);
        Account account = findAccountInUser(user, accountId);
        account.setApproved("accepted");
        return userRepository.save(user);
    }

    public User denyAccount(String userId, String accountId) {
        log.debug("Denying account {} for user {}", accountId, userId);
        User user = getUserById(userId);
        user.getAccounts().removeIf(a -> a.getAccountId().equals(accountId));
        if (user.getAccounts().isEmpty()) {
            userRepository.delete(user);
            return null;
        }
        return userRepository.save(user);
    }

    // ==================== Transaction Operations ====================

    public User deposit(String userId, String accountId, double amount) {
        log.debug("Depositing {} to account {} for user {}", amount, accountId, userId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        User user = getUserById(userId);
        Account account = findAccountInUser(user, accountId);
        account.setBalance(account.getBalance() + amount);
        return userRepository.save(user);
    }

    public User withdraw(String userId, String accountId, double amount) {
        log.debug("Withdrawing {} from account {} for user {}", amount, accountId, userId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        User user = getUserById(userId);
        Account account = findAccountInUser(user, accountId);
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds. Balance: " + account.getBalance());
        }
        account.setBalance(account.getBalance() - amount);
        return userRepository.save(user);
    }

    public void transfer(String fromUserId, String fromAccountId,
                         String toAccountId, double amount) {
        log.debug("Transferring {} from account {} to account {}", amount, fromAccountId, toAccountId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        User fromUser = getUserById(fromUserId);
        Account fromAccount = findAccountInUser(fromUser, fromAccountId);

        if (fromAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds. Balance: " + fromAccount.getBalance());
        }

        // Find the target account - could be in same user or different user
        User toUser = userRepository.findByAccountId(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Target account not found: " + toAccountId));
        Account toAccount = findAccountInUser(toUser, toAccountId);

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        userRepository.save(fromUser);
        if (!fromUser.getId().equals(toUser.getId())) {
            userRepository.save(toUser);
        }
    }

    // ==================== Helper Methods ====================

    private Account findAccountInUser(User user, String accountId) {
        return user.getAccounts().stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found: " + accountId + " for user: " + user.getId()));
    }
}

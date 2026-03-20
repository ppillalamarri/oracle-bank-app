package com.revature.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.bank.exception.DuplicateAccountException;
import com.revature.bank.exception.InsufficientFundsException;
import com.revature.bank.exception.ResourceNotFoundException;
import com.revature.bank.model.Account;
import com.revature.bank.model.Address;
import com.revature.bank.model.User;
import com.revature.bank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Account checkingsAccount;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        checkingsAccount = new Account("acc-1", 1000.0, "checkings", "accepted", 0);
        savingsAccount = new Account("acc-2", 500.0, "savings", "pending", 0);

        testUser = new User();
        testUser.setId("user-1");
        testUser.setUsername("johndoe");
        testUser.setPassword("password123");
        testUser.setFirstname("John");
        testUser.setLastname("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhone("555-1234");
        testUser.setType("customer");
        testUser.setAddress(new Address("123 Main St", "Springfield", "IL", "62701"));
        testUser.setAccounts(new ArrayList<>(Arrays.asList(checkingsAccount)));
    }

    // ==================== POST /api/login ====================

    @Test
    void login_Success_ReturnsUser() throws Exception {
        when(userService.login("johndoe", "password123")).thenReturn(testUser);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "johndoe", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.type").value("customer"));

        verify(userService).login("johndoe", "password123");
    }

    @Test
    void login_InvalidCredentials_Returns401() throws Exception {
        when(userService.login("johndoe", "wrongpass")).thenReturn(null);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "johndoe", "password", "wrongpass"))))
                .andExpect(status().isUnauthorized());

        verify(userService).login("johndoe", "wrongpass");
    }

    // ==================== POST /api/register ====================

    @Test
    void register_Success_Returns201() throws Exception {
        when(userService.registerUser(
                "John", "Doe", "johndoe", "password123",
                "john@example.com", "555-1234",
                "123 Main St", "Springfield", "IL", "62701",
                "checkings"))
                .thenReturn(testUser);

        Map<String, String> body = Map.ofEntries(
                Map.entry("firstname", "John"),
                Map.entry("lastname", "Doe"),
                Map.entry("username", "johndoe"),
                Map.entry("password", "password123"),
                Map.entry("email", "john@example.com"),
                Map.entry("phone", "555-1234"),
                Map.entry("street", "123 Main St"),
                Map.entry("city", "Springfield"),
                Map.entry("state", "IL"),
                Map.entry("zip", "62701"),
                Map.entry("accountType", "checkings")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.accounts", hasSize(1)));

        verify(userService).registerUser(
                "John", "Doe", "johndoe", "password123",
                "john@example.com", "555-1234",
                "123 Main St", "Springfield", "IL", "62701",
                "checkings");
    }

    @Test
    void register_DuplicateUsername_Returns409() throws Exception {
        when(userService.registerUser(
                any(), any(), eq("johndoe"), any(),
                any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DuplicateAccountException("Username 'johndoe' already exists"));

        Map<String, String> body = Map.ofEntries(
                Map.entry("firstname", "John"),
                Map.entry("lastname", "Doe"),
                Map.entry("username", "johndoe"),
                Map.entry("password", "password123"),
                Map.entry("email", "john@example.com"),
                Map.entry("phone", "555-1234"),
                Map.entry("street", "123 Main St"),
                Map.entry("city", "Springfield"),
                Map.entry("state", "IL"),
                Map.entry("zip", "62701"),
                Map.entry("accountType", "checkings")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username 'johndoe' already exists"));
    }

    // ==================== GET /api/users/{userId} ====================

    @Test
    void getUser_Success_ReturnsUser() throws Exception {
        when(userService.getUserById("user-1")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"))
                .andExpect(jsonPath("$.address.city").value("Springfield"))
                .andExpect(jsonPath("$.address.state").value("IL"))
                .andExpect(jsonPath("$.address.zip").value("62701"))
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].accountId").value("acc-1"))
                .andExpect(jsonPath("$.accounts[0].balance").value(1000.0));

        verify(userService).getUserById("user-1");
    }

    @Test
    void getUser_NotFound_Returns404() throws Exception {
        when(userService.getUserById("nonexistent"))
                .thenThrow(new ResourceNotFoundException("User not found with id: nonexistent"));

        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: nonexistent"));

        verify(userService).getUserById("nonexistent");
    }

    // ==================== GET /api/users ====================

    @Test
    void getAllCustomers_ReturnsCustomerList() throws Exception {
        User customer2 = new User();
        customer2.setId("user-2");
        customer2.setUsername("janedoe");
        customer2.setType("customer");
        customer2.setAccounts(new ArrayList<>());

        when(userService.getAllCustomers()).thenReturn(Arrays.asList(testUser, customer2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("user-1"))
                .andExpect(jsonPath("$[0].type").value("customer"))
                .andExpect(jsonPath("$[1].id").value("user-2"))
                .andExpect(jsonPath("$[1].type").value("customer"));

        verify(userService).getAllCustomers();
    }

    // ==================== GET /api/users/all ====================

    @Test
    void getAllUsers_ReturnsAllUsers() throws Exception {
        User admin = new User();
        admin.setId("admin-1");
        admin.setUsername("admin");
        admin.setType("admin");
        admin.setAccounts(new ArrayList<>());

        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser, admin));

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("user-1"))
                .andExpect(jsonPath("$[0].type").value("customer"))
                .andExpect(jsonPath("$[1].id").value("admin-1"))
                .andExpect(jsonPath("$[1].type").value("admin"));

        verify(userService).getAllUsers();
    }

    // ==================== DELETE /api/users/{userId} ====================

    @Test
    void deleteUser_Success_Returns204() throws Exception {
        doNothing().when(userService).deleteUser("user-1");

        mockMvc.perform(delete("/api/users/user-1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("user-1");
    }

    @Test
    void deleteUser_NotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: nonexistent"))
                .when(userService).deleteUser("nonexistent");

        mockMvc.perform(delete("/api/users/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: nonexistent"));

        verify(userService).deleteUser("nonexistent");
    }

    // ==================== POST /api/users/{userId}/accounts ====================

    @Test
    void addAccount_Success_Returns201() throws Exception {
        User userWithTwoAccounts = new User();
        userWithTwoAccounts.setId("user-1");
        userWithTwoAccounts.setUsername("johndoe");
        userWithTwoAccounts.setType("customer");
        userWithTwoAccounts.setAccounts(new ArrayList<>(Arrays.asList(checkingsAccount, savingsAccount)));

        when(userService.addAccount("user-1", "savings")).thenReturn(userWithTwoAccounts);

        mockMvc.perform(post("/api/users/user-1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountType", "savings"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accounts", hasSize(2)))
                .andExpect(jsonPath("$.accounts[0].type").value("checkings"))
                .andExpect(jsonPath("$.accounts[1].type").value("savings"));

        verify(userService).addAccount("user-1", "savings");
    }

    @Test
    void addAccount_DuplicateType_Returns409() throws Exception {
        when(userService.addAccount("user-1", "checkings"))
                .thenThrow(new DuplicateAccountException("User already has a checkings account"));

        mockMvc.perform(post("/api/users/user-1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountType", "checkings"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already has a checkings account"));

        verify(userService).addAccount("user-1", "checkings");
    }

    // ==================== GET /api/accounts/pending ====================

    @Test
    void getPendingAccounts_ReturnsPendingUsers() throws Exception {
        User pendingUser = new User();
        pendingUser.setId("user-3");
        pendingUser.setUsername("pending_user");
        pendingUser.setType("customer");
        Account pendingAccount = new Account("acc-3", 0.01, "checkings", "pending", 0);
        pendingUser.setAccounts(new ArrayList<>(Collections.singletonList(pendingAccount)));

        when(userService.getUsersWithPendingAccounts()).thenReturn(Collections.singletonList(pendingUser));

        mockMvc.perform(get("/api/accounts/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("user-3"))
                .andExpect(jsonPath("$[0].accounts[0].approved").value("pending"));

        verify(userService).getUsersWithPendingAccounts();
    }

    // ==================== PUT /api/users/{userId}/accounts/{accountId}/approve ====================

    @Test
    void approveAccount_Success_ReturnsUser() throws Exception {
        Account approvedAccount = new Account("acc-1", 1000.0, "checkings", "accepted", 0);
        User approvedUser = new User();
        approvedUser.setId("user-1");
        approvedUser.setUsername("johndoe");
        approvedUser.setType("customer");
        approvedUser.setAccounts(new ArrayList<>(Collections.singletonList(approvedAccount)));

        when(userService.approveAccount("user-1", "acc-1")).thenReturn(approvedUser);

        mockMvc.perform(put("/api/users/user-1/accounts/acc-1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.accounts[0].accountId").value("acc-1"))
                .andExpect(jsonPath("$.accounts[0].approved").value("accepted"));

        verify(userService).approveAccount("user-1", "acc-1");
    }

    // ==================== PUT /api/users/{userId}/accounts/{accountId}/deny ====================

    @Test
    void denyAccount_UserRemains_ReturnsUser() throws Exception {
        // User has two accounts; denying one leaves the user with one account
        Account remainingAccount = new Account("acc-2", 500.0, "savings", "accepted", 0);
        User remainingUser = new User();
        remainingUser.setId("user-1");
        remainingUser.setUsername("johndoe");
        remainingUser.setType("customer");
        remainingUser.setAccounts(new ArrayList<>(Collections.singletonList(remainingAccount)));

        when(userService.denyAccount("user-1", "acc-1")).thenReturn(remainingUser);

        mockMvc.perform(put("/api/users/user-1/accounts/acc-1/deny"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].accountId").value("acc-2"));

        verify(userService).denyAccount("user-1", "acc-1");
    }

    @Test
    void denyAccount_UserDeleted_ReturnsMessage() throws Exception {
        // User had only one account; denying it deletes the user (service returns null)
        when(userService.denyAccount("user-1", "acc-1")).thenReturn(null);

        mockMvc.perform(put("/api/users/user-1/accounts/acc-1/deny"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account denied and user deleted"));

        verify(userService).denyAccount("user-1", "acc-1");
    }

    // ==================== POST /api/users/{userId}/accounts/{accountId}/deposit ====================

    @Test
    void deposit_Success_ReturnsUpdatedUser() throws Exception {
        Account depositedAccount = new Account("acc-1", 1500.0, "checkings", "accepted", 0);
        User updatedUser = new User();
        updatedUser.setId("user-1");
        updatedUser.setUsername("johndoe");
        updatedUser.setType("customer");
        updatedUser.setAccounts(new ArrayList<>(Collections.singletonList(depositedAccount)));

        when(userService.deposit("user-1", "acc-1", 500.0)).thenReturn(updatedUser);

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 500.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].balance").value(1500.0));

        verify(userService).deposit("user-1", "acc-1", 500.0);
    }

    @Test
    void deposit_NegativeAmount_Returns400() throws Exception {
        when(userService.deposit("user-1", "acc-1", -100.0))
                .thenThrow(new IllegalArgumentException("Deposit amount must be positive"));

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", -100.0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Deposit amount must be positive"));

        verify(userService).deposit("user-1", "acc-1", -100.0);
    }

    // ==================== POST /api/users/{userId}/accounts/{accountId}/withdraw ====================

    @Test
    void withdraw_Success_ReturnsUpdatedUser() throws Exception {
        Account withdrawnAccount = new Account("acc-1", 700.0, "checkings", "accepted", 0);
        User updatedUser = new User();
        updatedUser.setId("user-1");
        updatedUser.setUsername("johndoe");
        updatedUser.setType("customer");
        updatedUser.setAccounts(new ArrayList<>(Collections.singletonList(withdrawnAccount)));

        when(userService.withdraw("user-1", "acc-1", 300.0)).thenReturn(updatedUser);

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 300.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].balance").value(700.0));

        verify(userService).withdraw("user-1", "acc-1", 300.0);
    }

    @Test
    void withdraw_InsufficientFunds_Returns400() throws Exception {
        when(userService.withdraw("user-1", "acc-1", 5000.0))
                .thenThrow(new InsufficientFundsException("Insufficient funds. Balance: 1000.0"));

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 5000.0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds. Balance: 1000.0"));

        verify(userService).withdraw("user-1", "acc-1", 5000.0);
    }

    // ==================== POST /api/users/{userId}/accounts/{accountId}/transfer ====================

    @Test
    void transfer_Success_ReturnsMessage() throws Exception {
        doNothing().when(userService).transfer("user-1", "acc-1", "acc-2", 200.0);

        Map<String, String> body = Map.of("toAccountId", "acc-2", "amount", "200.0");

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer successful"));

        verify(userService).transfer("user-1", "acc-1", "acc-2", 200.0);
    }

    @Test
    void transfer_InsufficientFunds_Returns400() throws Exception {
        doThrow(new InsufficientFundsException("Insufficient funds. Balance: 1000.0"))
                .when(userService).transfer("user-1", "acc-1", "acc-2", 5000.0);

        Map<String, String> body = Map.of("toAccountId", "acc-2", "amount", "5000.0");

        mockMvc.perform(post("/api/users/user-1/accounts/acc-1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds. Balance: 1000.0"));

        verify(userService).transfer("user-1", "acc-1", "acc-2", 5000.0);
    }
}

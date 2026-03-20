package com.revature.bank.service;

import com.revature.bank.exception.DuplicateAccountException;
import com.revature.bank.exception.InsufficientFundsException;
import com.revature.bank.exception.ResourceNotFoundException;
import com.revature.bank.model.Account;
import com.revature.bank.model.Address;
import com.revature.bank.model.User;
import com.revature.bank.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ==================== Helper Methods ====================

    private User createTestUser(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password123");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail(username + "@example.com");
        user.setPhone("555-1234");
        user.setType("customer");
        user.setAddress(createTestAddress());
        user.setAccounts(new ArrayList<>());
        return user;
    }

    private Address createTestAddress() {
        return new Address("123 Main St", "Springfield", "IL", "62701");
    }

    private Account createTestAccount(String accountId, String type, double balance, String approved) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setType(type);
        account.setBalance(balance);
        account.setApproved(approved);
        account.setJoint(0);
        return account;
    }

    private User createUserWithCheckingsAccount() {
        User user = createTestUser("user1", "johndoe");
        Account checkings = createTestAccount("acct1", "checkings", 100.0, "accepted");
        user.getAccounts().add(checkings);
        return user;
    }

    private User createUserWithBothAccounts() {
        User user = createUserWithCheckingsAccount();
        Account savings = createTestAccount("acct2", "savings", 200.0, "accepted");
        user.getAccounts().add(savings);
        return user;
    }

    // ==================== User Operations ====================

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        @DisplayName("should return user on valid credentials")
        void loginSuccess() {
            User user = createTestUser("user1", "johndoe");
            when(userRepository.findByUsernameAndPassword("johndoe", "password123"))
                    .thenReturn(Optional.of(user));

            User result = userService.login("johndoe", "password123");

            assertNotNull(result);
            assertEquals("johndoe", result.getUsername());
            verify(userRepository).findByUsernameAndPassword("johndoe", "password123");
        }

        @Test
        @DisplayName("should return null on invalid credentials")
        void loginFailure() {
            when(userRepository.findByUsernameAndPassword("johndoe", "wrongpass"))
                    .thenReturn(Optional.empty());

            User result = userService.login("johndoe", "wrongpass");

            assertNull(result);
            verify(userRepository).findByUsernameAndPassword("johndoe", "wrongpass");
        }
    }

    @Nested
    @DisplayName("registerUser")
    class RegisterUserTests {

        @Test
        @DisplayName("should register a new user successfully")
        void registerUserSuccess() {
            when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId("generatedId");
                return saved;
            });

            User result = userService.registerUser(
                    "John", "Doe", "newuser", "pass123",
                    "john@example.com", "555-1234",
                    "123 Main St", "Springfield", "IL", "62701",
                    "checkings"
            );

            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("John", result.getFirstname());
            assertEquals("Doe", result.getLastname());
            assertEquals("pass123", result.getPassword());
            assertEquals("john@example.com", result.getEmail());
            assertEquals("555-1234", result.getPhone());
            assertEquals("customer", result.getType());
            assertNotNull(result.getAddress());
            assertEquals("123 Main St", result.getAddress().getStreet());
            assertEquals("Springfield", result.getAddress().getCity());
            assertEquals("IL", result.getAddress().getState());
            assertEquals("62701", result.getAddress().getZip());
            assertEquals(1, result.getAccounts().size());
            Account account = result.getAccounts().get(0);
            assertNotNull(account.getAccountId());
            assertEquals(0.01, account.getBalance());
            assertEquals("checkings", account.getType());
            assertEquals("pending", account.getApproved());
            assertEquals(0, account.getJoint());

            verify(userRepository).findByUsername("newuser");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw DuplicateAccountException for duplicate username")
        void registerUserDuplicateUsername() {
            User existing = createTestUser("user1", "existing");
            when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existing));

            assertThrows(DuplicateAccountException.class, () ->
                    userService.registerUser(
                            "Jane", "Doe", "existing", "pass123",
                            "jane@example.com", "555-5678",
                            "456 Oak Ave", "Chicago", "IL", "60601",
                            "savings"
                    )
            );

            verify(userRepository).findByUsername("existing");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("should return user when found")
        void getUserByIdFound() {
            User user = createTestUser("user1", "johndoe");
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            User result = userService.getUserById("user1");

            assertNotNull(result);
            assertEquals("user1", result.getId());
            assertEquals("johndoe", result.getUsername());
            verify(userRepository).findById("user1");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void getUserByIdNotFound() {
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.getUserById("nonexistent")
            );

            verify(userRepository).findById("nonexistent");
        }
    }

    @Nested
    @DisplayName("getAllCustomers")
    class GetAllCustomersTests {

        @Test
        @DisplayName("should return all customers")
        void getAllCustomers() {
            User customer1 = createTestUser("c1", "customer1");
            User customer2 = createTestUser("c2", "customer2");
            when(userRepository.findByType("customer")).thenReturn(Arrays.asList(customer1, customer2));

            List<User> result = userService.getAllCustomers();

            assertEquals(2, result.size());
            verify(userRepository).findByType("customer");
        }

        @Test
        @DisplayName("should return empty list when no customers exist")
        void getAllCustomersEmpty() {
            when(userRepository.findByType("customer")).thenReturn(Collections.emptyList());

            List<User> result = userService.getAllCustomers();

            assertTrue(result.isEmpty());
            verify(userRepository).findByType("customer");
        }
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return all users")
        void getAllUsers() {
            User admin = createTestUser("a1", "admin");
            admin.setType("admin");
            User customer = createTestUser("c1", "customer1");
            when(userRepository.findAll()).thenReturn(Arrays.asList(admin, customer));

            List<User> result = userService.getAllUsers();

            assertEquals(2, result.size());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUserTests {

        @Test
        @DisplayName("should delete user when exists")
        void deleteUserExists() {
            User user = createTestUser("user1", "johndoe");
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            userService.deleteUser("user1");

            verify(userRepository).findById("user1");
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void deleteUserNotExists() {
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.deleteUser("nonexistent")
            );

            verify(userRepository).findById("nonexistent");
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    // ==================== Account Operations ====================

    @Nested
    @DisplayName("addAccount")
    class AddAccountTests {

        @Test
        @DisplayName("should add savings account when user only has checkings")
        void addAccountSuccess() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.addAccount("user1", "savings");

            assertEquals(2, result.getAccounts().size());
            Account newAccount = result.getAccounts().get(1);
            assertEquals("savings", newAccount.getType());
            assertEquals(0.01, newAccount.getBalance());
            assertNotNull(newAccount.getAccountId());
            assertEquals(0, newAccount.getJoint());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw DuplicateAccountException when user already has both types")
        void addAccountAlreadyHasBothTypes() {
            User user = createUserWithBothAccounts();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(DuplicateAccountException.class, () ->
                    userService.addAccount("user1", "checkings")
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw DuplicateAccountException when user already has checkings and requests checkings")
        void addDuplicateCheckingsAccount() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(DuplicateAccountException.class, () ->
                    userService.addAccount("user1", "checkings")
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw DuplicateAccountException when user already has savings and requests savings")
        void addDuplicateSavingsAccount() {
            User user = createTestUser("user1", "johndoe");
            Account savings = createTestAccount("acct1", "savings", 50.0, "pending");
            user.getAccounts().add(savings);
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(DuplicateAccountException.class, () ->
                    userService.addAccount("user1", "savings")
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should auto-approve secondary account when primary is accepted")
        void addAccountAutoApproveWhenPrimaryApproved() {
            User user = createUserWithCheckingsAccount(); // checkings is "accepted"
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.addAccount("user1", "savings");

            Account newAccount = result.getAccounts().get(1);
            assertEquals("accepted", newAccount.getApproved());
        }

        @Test
        @DisplayName("should set pending status when primary account is not approved")
        void addAccountPendingWhenPrimaryNotApproved() {
            User user = createTestUser("user1", "johndoe");
            Account checkings = createTestAccount("acct1", "checkings", 100.0, "pending");
            user.getAccounts().add(checkings);
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.addAccount("user1", "savings");

            Account newAccount = result.getAccounts().get(1);
            assertEquals("pending", newAccount.getApproved());
        }
    }

    @Nested
    @DisplayName("getUsersWithPendingAccounts")
    class GetUsersWithPendingAccountsTests {

        @Test
        @DisplayName("should return users with pending accounts")
        void getUsersWithPendingAccounts() {
            User user1 = createTestUser("u1", "user1");
            user1.getAccounts().add(createTestAccount("a1", "checkings", 100.0, "pending"));
            User user2 = createTestUser("u2", "user2");
            user2.getAccounts().add(createTestAccount("a2", "savings", 50.0, "pending"));
            when(userRepository.findUsersWithPendingAccounts()).thenReturn(Arrays.asList(user1, user2));

            List<User> result = userService.getUsersWithPendingAccounts();

            assertEquals(2, result.size());
            verify(userRepository).findUsersWithPendingAccounts();
        }

        @Test
        @DisplayName("should return empty list when no pending accounts")
        void getUsersWithPendingAccountsEmpty() {
            when(userRepository.findUsersWithPendingAccounts()).thenReturn(Collections.emptyList());

            List<User> result = userService.getUsersWithPendingAccounts();

            assertTrue(result.isEmpty());
            verify(userRepository).findUsersWithPendingAccounts();
        }
    }

    @Nested
    @DisplayName("approveAccount")
    class ApproveAccountTests {

        @Test
        @DisplayName("should approve a pending account")
        void approveAccountSuccess() {
            User user = createTestUser("user1", "johndoe");
            Account account = createTestAccount("acct1", "checkings", 100.0, "pending");
            user.getAccounts().add(account);
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.approveAccount("user1", "acct1");

            assertEquals("accepted", result.getAccounts().get(0).getApproved());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for non-existent account")
        void approveAccountNotFound() {
            User user = createTestUser("user1", "johndoe");
            user.getAccounts().add(createTestAccount("acct1", "checkings", 100.0, "pending"));
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.approveAccount("user1", "nonexistent")
            );

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("denyAccount")
    class DenyAccountTests {

        @Test
        @DisplayName("should remove account and keep user when user has remaining accounts")
        void denyAccountWithRemainingAccounts() {
            User user = createUserWithBothAccounts();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.denyAccount("user1", "acct1");

            assertNotNull(result);
            assertEquals(1, result.getAccounts().size());
            assertEquals("acct2", result.getAccounts().get(0).getAccountId());
            verify(userRepository).save(user);
            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        @DisplayName("should delete user when last account is denied")
        void denyAccountLastAccountTriggersUserDeletion() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            User result = userService.denyAccount("user1", "acct1");

            assertNull(result);
            verify(userRepository).delete(user);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should not remove any account if accountId does not match")
        void denyAccountNonMatchingId() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.denyAccount("user1", "nonexistent");

            assertNotNull(result);
            assertEquals(1, result.getAccounts().size());
            verify(userRepository).save(user);
        }
    }

    // ==================== Transaction Operations ====================

    @Nested
    @DisplayName("deposit")
    class DepositTests {

        @Test
        @DisplayName("should deposit amount successfully")
        void depositSuccess() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.deposit("user1", "acct1", 50.0);

            assertEquals(150.0, result.getAccounts().get(0).getBalance(), 0.001);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for negative deposit amount")
        void depositNegativeAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.deposit("user1", "acct1", -10.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for zero deposit amount")
        void depositZeroAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.deposit("user1", "acct1", 0.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for non-existent account")
        void depositAccountNotFound() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.deposit("user1", "nonexistent", 50.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("withdraw")
    class WithdrawTests {

        @Test
        @DisplayName("should withdraw amount successfully")
        void withdrawSuccess() {
            User user = createUserWithCheckingsAccount(); // balance = 100.0
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.withdraw("user1", "acct1", 30.0);

            assertEquals(70.0, result.getAccounts().get(0).getBalance(), 0.001);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw InsufficientFundsException when balance is too low")
        void withdrawInsufficientFunds() {
            User user = createUserWithCheckingsAccount(); // balance = 100.0
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(InsufficientFundsException.class, () ->
                    userService.withdraw("user1", "acct1", 200.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for negative withdrawal amount")
        void withdrawNegativeAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.withdraw("user1", "acct1", -10.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for zero withdrawal amount")
        void withdrawZeroAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.withdraw("user1", "acct1", 0.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for non-existent account")
        void withdrawAccountNotFound() {
            User user = createUserWithCheckingsAccount();
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.withdraw("user1", "nonexistent", 10.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("transfer")
    class TransferTests {

        @Test
        @DisplayName("should transfer between accounts of different users successfully")
        void transferCrossUserSuccess() {
            User fromUser = createTestUser("user1", "john");
            fromUser.getAccounts().add(createTestAccount("acctFrom", "checkings", 500.0, "accepted"));

            User toUser = createTestUser("user2", "jane");
            toUser.getAccounts().add(createTestAccount("acctTo", "savings", 100.0, "accepted"));

            when(userRepository.findById("user1")).thenReturn(Optional.of(fromUser));
            when(userRepository.findByAccountId("acctTo")).thenReturn(Optional.of(toUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.transfer("user1", "acctFrom", "acctTo", 200.0);

            assertEquals(300.0, fromUser.getAccounts().get(0).getBalance(), 0.001);
            assertEquals(300.0, toUser.getAccounts().get(0).getBalance(), 0.001);
            verify(userRepository).save(fromUser);
            verify(userRepository).save(toUser);
        }

        @Test
        @DisplayName("should transfer between accounts of the same user")
        void transferSameUserBetweenAccounts() {
            User user = createUserWithBothAccounts(); // checkings=100, savings=200
            when(userRepository.findById("user1")).thenReturn(Optional.of(user));
            when(userRepository.findByAccountId("acct2")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.transfer("user1", "acct1", "acct2", 50.0);

            assertEquals(50.0, user.getAccounts().get(0).getBalance(), 0.001);
            assertEquals(250.0, user.getAccounts().get(1).getBalance(), 0.001);
            // save should only be called once for same user
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("should throw InsufficientFundsException when source has insufficient funds")
        void transferInsufficientFunds() {
            User fromUser = createTestUser("user1", "john");
            fromUser.getAccounts().add(createTestAccount("acctFrom", "checkings", 50.0, "accepted"));

            when(userRepository.findById("user1")).thenReturn(Optional.of(fromUser));

            assertThrows(InsufficientFundsException.class, () ->
                    userService.transfer("user1", "acctFrom", "acctTo", 200.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for negative transfer amount")
        void transferNegativeAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.transfer("user1", "acctFrom", "acctTo", -10.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for zero transfer amount")
        void transferZeroAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.transfer("user1", "acctFrom", "acctTo", 0.0)
            );

            verify(userRepository, never()).findById(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when target account not found")
        void transferTargetAccountNotFound() {
            User fromUser = createTestUser("user1", "john");
            fromUser.getAccounts().add(createTestAccount("acctFrom", "checkings", 500.0, "accepted"));

            when(userRepository.findById("user1")).thenReturn(Optional.of(fromUser));
            when(userRepository.findByAccountId("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.transfer("user1", "acctFrom", "nonexistent", 100.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when source account not found in user")
        void transferSourceAccountNotFound() {
            User fromUser = createTestUser("user1", "john");
            fromUser.getAccounts().add(createTestAccount("acctFrom", "checkings", 500.0, "accepted"));

            when(userRepository.findById("user1")).thenReturn(Optional.of(fromUser));

            assertThrows(ResourceNotFoundException.class, () ->
                    userService.transfer("user1", "wrongAcct", "acctTo", 100.0)
            );

            verify(userRepository, never()).save(any(User.class));
        }
    }
}

package com.revature.bank.repository;

import com.revature.bank.model.Account;
import com.revature.bank.model.Address;
import com.revature.bank.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository integration tests for {@link UserRepository} using embedded MongoDB.
 */
@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User customerUser;
    private User adminUser;
    private User employeeUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Customer with one pending and one approved account
        customerUser = new User();
        customerUser.setUsername("jdoe");
        customerUser.setPassword("password123");
        customerUser.setFirstname("John");
        customerUser.setLastname("Doe");
        customerUser.setEmail("jdoe@example.com");
        customerUser.setPhone("555-1234");
        customerUser.setType("customer");
        customerUser.setAddress(new Address("123 Main St", "Springfield", "IL", "62704"));
        customerUser.setAccounts(Arrays.asList(
                new Account("ACC001", 1000.0, "checking", "pending", 0),
                new Account("ACC002", 5000.0, "savings", "accepted", 0)
        ));
        customerUser = userRepository.save(customerUser);

        // Admin with no accounts
        adminUser = new User();
        adminUser.setUsername("admin1");
        adminUser.setPassword("adminpass");
        adminUser.setFirstname("Alice");
        adminUser.setLastname("Admin");
        adminUser.setEmail("alice@bank.com");
        adminUser.setPhone("555-5678");
        adminUser.setType("admin");
        adminUser.setAddress(new Address("456 Oak Ave", "Chicago", "IL", "60601"));
        adminUser.setAccounts(Collections.emptyList());
        adminUser = userRepository.save(adminUser);

        // Employee with all-approved accounts
        employeeUser = new User();
        employeeUser.setUsername("emp1");
        employeeUser.setPassword("emppass");
        employeeUser.setFirstname("Bob");
        employeeUser.setLastname("Employee");
        employeeUser.setEmail("bob@bank.com");
        employeeUser.setPhone("555-9012");
        employeeUser.setType("employee");
        employeeUser.setAddress(new Address("789 Pine Rd", "Peoria", "IL", "61602"));
        employeeUser.setAccounts(Arrays.asList(
                new Account("ACC003", 2500.0, "checking", "accepted", 0),
                new Account("ACC004", 10000.0, "savings", "accepted", 1)
        ));
        employeeUser = userRepository.save(employeeUser);
    }

    // ==================== findByUsernameAndPassword ====================

    @Test
    void findByUsernameAndPassword_validCredentials_returnsUser() {
        Optional<User> result = userRepository.findByUsernameAndPassword("jdoe", "password123");

        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
        assertEquals("John", result.get().getFirstname());
    }

    @Test
    void findByUsernameAndPassword_wrongPassword_returnsEmpty() {
        Optional<User> result = userRepository.findByUsernameAndPassword("jdoe", "wrongpass");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsernameAndPassword_wrongUsername_returnsEmpty() {
        Optional<User> result = userRepository.findByUsernameAndPassword("nonexistent", "password123");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsernameAndPassword_bothInvalid_returnsEmpty() {
        Optional<User> result = userRepository.findByUsernameAndPassword("nouser", "nopass");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsernameAndPassword_nullUsername_returnsEmpty() {
        Optional<User> result = userRepository.findByUsernameAndPassword(null, "password123");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsernameAndPassword_nullPassword_returnsEmpty() {
        Optional<User> result = userRepository.findByUsernameAndPassword("jdoe", null);

        assertFalse(result.isPresent());
    }

    // ==================== findByUsername ====================

    @Test
    void findByUsername_existingUser_returnsUser() {
        Optional<User> result = userRepository.findByUsername("admin1");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstname());
        assertEquals("admin", result.get().getType());
    }

    @Test
    void findByUsername_nonExisting_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("ghost");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_nullUsername_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername(null);

        assertFalse(result.isPresent());
    }

    // ==================== findByType ====================

    @Test
    void findByType_customer_returnsCustomers() {
        List<User> customers = userRepository.findByType("customer");

        assertEquals(1, customers.size());
        assertEquals("jdoe", customers.get(0).getUsername());
    }

    @Test
    void findByType_admin_returnsAdmins() {
        List<User> admins = userRepository.findByType("admin");

        assertEquals(1, admins.size());
        assertEquals("admin1", admins.get(0).getUsername());
    }

    @Test
    void findByType_employee_returnsEmployees() {
        List<User> employees = userRepository.findByType("employee");

        assertEquals(1, employees.size());
        assertEquals("emp1", employees.get(0).getUsername());
    }

    @Test
    void findByType_nonExistingType_returnsEmptyList() {
        List<User> result = userRepository.findByType("manager");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByType_multipleUsersOfSameType_returnsAll() {
        User anotherCustomer = new User();
        anotherCustomer.setUsername("jsmith");
        anotherCustomer.setPassword("pass456");
        anotherCustomer.setFirstname("Jane");
        anotherCustomer.setLastname("Smith");
        anotherCustomer.setEmail("jsmith@example.com");
        anotherCustomer.setType("customer");
        userRepository.save(anotherCustomer);

        List<User> customers = userRepository.findByType("customer");

        assertEquals(2, customers.size());
    }

    // ==================== findUsersWithPendingAccounts ====================

    @Test
    void findUsersWithPendingAccounts_returnsUsersWithPending() {
        List<User> pending = userRepository.findUsersWithPendingAccounts();

        assertEquals(1, pending.size());
        assertEquals("jdoe", pending.get(0).getUsername());
    }

    @Test
    void findUsersWithPendingAccounts_noPending_returnsEmpty() {
        // Remove the customer who has the pending account
        userRepository.delete(customerUser);

        List<User> pending = userRepository.findUsersWithPendingAccounts();

        assertTrue(pending.isEmpty());
    }

    @Test
    void findUsersWithPendingAccounts_multiplePendingUsers_returnsAll() {
        User anotherUser = new User();
        anotherUser.setUsername("pending2");
        anotherUser.setPassword("pass");
        anotherUser.setFirstname("Pending");
        anotherUser.setLastname("User");
        anotherUser.setEmail("pending2@example.com");
        anotherUser.setType("customer");
        anotherUser.setAccounts(Collections.singletonList(
                new Account("ACC010", 100.0, "checking", "pending", 0)
        ));
        userRepository.save(anotherUser);

        List<User> pending = userRepository.findUsersWithPendingAccounts();

        assertEquals(2, pending.size());
    }

    @Test
    void findUsersWithPendingAccounts_userWithOnlyAcceptedAccounts_notReturned() {
        // employeeUser has only accepted accounts
        List<User> pending = userRepository.findUsersWithPendingAccounts();

        boolean containsEmployee = pending.stream()
                .anyMatch(u -> u.getUsername().equals("emp1"));
        assertFalse(containsEmployee);
    }

    @Test
    void findUsersWithPendingAccounts_userWithNoAccounts_notReturned() {
        // adminUser has empty accounts list
        List<User> pending = userRepository.findUsersWithPendingAccounts();

        boolean containsAdmin = pending.stream()
                .anyMatch(u -> u.getUsername().equals("admin1"));
        assertFalse(containsAdmin);
    }

    // ==================== findByAccountId ====================

    @Test
    void findByAccountId_existingAccountId_returnsUser() {
        Optional<User> result = userRepository.findByAccountId("ACC001");

        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
    }

    @Test
    void findByAccountId_secondAccount_returnsUser() {
        Optional<User> result = userRepository.findByAccountId("ACC002");

        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
    }

    @Test
    void findByAccountId_differentUser_returnsCorrectUser() {
        Optional<User> result = userRepository.findByAccountId("ACC003");

        assertTrue(result.isPresent());
        assertEquals("emp1", result.get().getUsername());
    }

    @Test
    void findByAccountId_nonExistingAccountId_returnsEmpty() {
        Optional<User> result = userRepository.findByAccountId("ACC999");

        assertFalse(result.isPresent());
    }

    @Test
    void findByAccountId_nullAccountId_returnsEmpty() {
        Optional<User> result = userRepository.findByAccountId(null);

        assertFalse(result.isPresent());
    }

    // ==================== Standard CRUD: save ====================

    @Test
    void save_newUser_persistsSuccessfully() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");
        newUser.setFirstname("New");
        newUser.setLastname("User");
        newUser.setEmail("new@example.com");
        newUser.setPhone("555-0000");
        newUser.setType("customer");

        User saved = userRepository.save(newUser);

        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("New", found.get().getFirstname());
    }

    @Test
    void save_userWithEmbeddedAddress_persistsAddress() {
        User user = new User();
        user.setUsername("addressuser");
        user.setPassword("pass");
        user.setFirstname("Addr");
        user.setLastname("User");
        user.setEmail("addr@example.com");
        user.setType("customer");
        user.setAddress(new Address("100 Test Ln", "TestCity", "TS", "00000"));

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertNotNull(found.get().getAddress());
        assertEquals("100 Test Ln", found.get().getAddress().getStreet());
        assertEquals("TestCity", found.get().getAddress().getCity());
        assertEquals("TS", found.get().getAddress().getState());
        assertEquals("00000", found.get().getAddress().getZip());
    }

    @Test
    void save_userWithEmbeddedAccounts_persistsAccounts() {
        User user = new User();
        user.setUsername("accuser");
        user.setPassword("pass");
        user.setFirstname("Acc");
        user.setLastname("User");
        user.setEmail("acc@example.com");
        user.setType("customer");
        user.setAccounts(Arrays.asList(
                new Account("A1", 500.0, "checking", "accepted", 0),
                new Account("A2", 1500.0, "savings", "pending", 1)
        ));

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(2, found.get().getAccounts().size());
        assertEquals("A1", found.get().getAccounts().get(0).getAccountId());
        assertEquals(500.0, found.get().getAccounts().get(0).getBalance());
        assertEquals("A2", found.get().getAccounts().get(1).getAccountId());
    }

    @Test
    void save_updateExistingUser_updatesFields() {
        customerUser.setFirstname("Jonathan");
        customerUser.setPhone("555-9999");
        userRepository.save(customerUser);

        Optional<User> found = userRepository.findById(customerUser.getId());

        assertTrue(found.isPresent());
        assertEquals("Jonathan", found.get().getFirstname());
        assertEquals("555-9999", found.get().getPhone());
    }

    @Test
    void save_userWithNullAddress_persistsWithoutAddress() {
        User user = new User();
        user.setUsername("noaddr");
        user.setPassword("pass");
        user.setFirstname("No");
        user.setLastname("Addr");
        user.setEmail("noaddr@example.com");
        user.setType("customer");
        user.setAddress(null);

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertNull(found.get().getAddress());
    }

    // ==================== Standard CRUD: findById ====================

    @Test
    void findById_existingId_returnsUser() {
        Optional<User> result = userRepository.findById(customerUser.getId());

        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<User> result = userRepository.findById("nonexistent-id");

        assertFalse(result.isPresent());
    }

    // ==================== Standard CRUD: findAll ====================

    @Test
    void findAll_returnsAllUsers() {
        List<User> users = userRepository.findAll();

        assertEquals(3, users.size());
    }

    @Test
    void findAll_emptyCollection_returnsEmptyList() {
        userRepository.deleteAll();

        List<User> users = userRepository.findAll();

        assertTrue(users.isEmpty());
    }

    // ==================== Standard CRUD: delete ====================

    @Test
    void delete_existingUser_removesFromCollection() {
        userRepository.delete(customerUser);

        Optional<User> result = userRepository.findById(customerUser.getId());
        assertFalse(result.isPresent());
        assertEquals(2, userRepository.count());
    }

    @Test
    void deleteById_existingId_removesUser() {
        userRepository.deleteById(adminUser.getId());

        assertFalse(userRepository.findById(adminUser.getId()).isPresent());
        assertEquals(2, userRepository.count());
    }

    @Test
    void deleteAll_removesAllUsers() {
        userRepository.deleteAll();

        assertEquals(0, userRepository.count());
        assertTrue(userRepository.findAll().isEmpty());
    }

    // ==================== Edge cases ====================

    @Test
    void save_userWithEmptyAccountsList_persistsSuccessfully() {
        User user = new User();
        user.setUsername("emptyacc");
        user.setPassword("pass");
        user.setFirstname("Empty");
        user.setLastname("Accounts");
        user.setEmail("empty@example.com");
        user.setType("customer");
        user.setAccounts(Collections.emptyList());

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertTrue(found.get().getAccounts().isEmpty());
    }

    @Test
    void findByType_nullType_returnsEmptyList() {
        List<User> result = userRepository.findByType(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void count_returnsCorrectCount() {
        assertEquals(3, userRepository.count());
    }

    @Test
    void save_userWithJointAccount_persistsJointFlag() {
        Optional<User> result = userRepository.findByAccountId("ACC004");

        assertTrue(result.isPresent());
        Account jointAccount = result.get().getAccounts().stream()
                .filter(a -> a.getAccountId().equals("ACC004"))
                .findFirst()
                .orElse(null);
        assertNotNull(jointAccount);
        assertEquals(1, jointAccount.getJoint());
    }

    @Test
    void save_multipleUsersWithDifferentTypes_allRetrievable() {
        List<User> customers = userRepository.findByType("customer");
        List<User> admins = userRepository.findByType("admin");
        List<User> employees = userRepository.findByType("employee");

        assertEquals(1, customers.size());
        assertEquals(1, admins.size());
        assertEquals(1, employees.size());
        assertEquals(3, customers.size() + admins.size() + employees.size());
    }
}

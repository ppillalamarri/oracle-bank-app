package com.revature.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MongoDB document representing a bank user.
 * Migrated from Oracle user_table and account_table.
 *
 * Original Oracle schema foreign keys:
 *   user_table.account_table_id  -> account_table.account_id
 *   user_table.account_table_id2 -> account_table.account_id
 *
 * In MongoDB, the referenced account rows are embedded directly as a list
 * of Account sub-documents within the User document. The Address fields
 * (address, city, state, zip) are also embedded as an Address sub-document.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String type; // admin, employee, customer

    /** Embedded address (was flat columns in Oracle user_table) */
    private Address address;

    /** Embedded accounts (replaces FK references to account_table) */
    private List<Account> accounts = new ArrayList<>();

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username
                + "', firstname='" + firstname + "', lastname='" + lastname
                + "', type='" + type + "', email='" + email
                + "', phone='" + phone + "', address=" + address
                + "', accounts=" + accounts + "}";
    }
}

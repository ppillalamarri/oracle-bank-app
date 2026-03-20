package com.revature.bank.model;

import java.util.Objects;

/**
 * Embedded document representing a bank account.
 * Migrated from Oracle account_table. In the original schema, user_table had
 * foreign keys (account_table_id, account_table_id2) referencing account_table.
 * In MongoDB, accounts are embedded directly within the User document.
 */
public class Account {

    private String accountId;
    private Double balance;
    private String type;
    private String approved;
    private int joint;

    public Account() {
    }

    public Account(String accountId, Double balance, String type, String approved, int joint) {
        this.accountId = accountId;
        this.balance = balance;
        this.type = type;
        this.approved = approved;
        this.joint = joint;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public int getJoint() {
        return joint;
    }

    public void setJoint(int joint) {
        this.joint = joint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return joint == account.joint
                && Objects.equals(accountId, account.accountId)
                && Objects.equals(balance, account.balance)
                && Objects.equals(type, account.type)
                && Objects.equals(approved, account.approved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, balance, type, approved, joint);
    }

    @Override
    public String toString() {
        return "Account{accountId='" + accountId + "', balance=" + balance
                + ", type='" + type + "', approved='" + approved + "', joint=" + joint + "}";
    }
}

package com.revature.bank.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String sortCode, String accountNumber) {
        super("Account not found: " + sortCode + "/" + accountNumber);
    }
}

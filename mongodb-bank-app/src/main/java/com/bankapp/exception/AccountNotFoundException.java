package com.bankapp.exception;

/**
 * Thrown when an account cannot be found.
 * Equivalent to COBOL NOTFND condition or SQLCODE +100.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String sortCode, String accountNumber) {
        super("Account not found: sortCode=" + sortCode + ", accountNumber=" + accountNumber);
    }
}

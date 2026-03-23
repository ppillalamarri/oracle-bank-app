package com.bankapp.exception;

/**
 * Thrown when a debit/transfer cannot proceed due to insufficient funds.
 * Equivalent to COBOL DBCRFUN fail-code '3'.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber) {
        super("Insufficient funds in account: " + accountNumber);
    }
}

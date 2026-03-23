package com.bankapp.exception;

/**
 * Thrown when a customer cannot be found.
 * Equivalent to COBOL NOTFND condition on VSAM READ.
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerNumber) {
        super("Customer not found: customerNumber=" + customerNumber);
    }
}

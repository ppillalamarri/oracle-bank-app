package com.revature.bank.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerNumber) {
        super("Customer not found: " + customerNumber);
    }
}

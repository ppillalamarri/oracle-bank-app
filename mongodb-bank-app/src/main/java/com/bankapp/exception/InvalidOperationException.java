package com.bankapp.exception;

/**
 * Thrown when a requested banking operation is not permitted.
 * Covers scenarios like transferring to the same account (COBOL XFRFUN abend 'SAME'),
 * exceeding max accounts per customer (CREACC fail-code '8'),
 * or debit/credit on MORTGAGE/LOAN accounts (DBCRFUN fail-code '4').
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}

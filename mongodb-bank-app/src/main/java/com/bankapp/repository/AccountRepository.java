package com.bankapp.repository;

import com.bankapp.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for Account documents.
 *
 * Replaces COBOL DB2 ACCOUNT table access patterns from:
 *   - INQACC.cbl (SELECT by sortCode + accountNumber)
 *   - INQACCCU.cbl (SELECT by customerNumber)
 *   - CREACC.cbl (INSERT)
 *   - UPDACC.cbl (UPDATE)
 *   - DELACC.cbl (DELETE)
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    Optional<Account> findBySortCodeAndAccountNumber(String sortCode, String accountNumber);

    List<Account> findByCustomerNumber(String customerNumber);

    long countByCustomerNumber(String customerNumber);

    void deleteBySortCodeAndAccountNumber(String sortCode, String accountNumber);

    Optional<Account> findTopBySortCodeOrderByAccountNumberDesc(String sortCode);
}

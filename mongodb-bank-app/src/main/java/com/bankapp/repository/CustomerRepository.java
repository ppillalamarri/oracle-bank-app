package com.bankapp.repository;

import com.bankapp.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for Customer documents.
 *
 * Replaces COBOL VSAM CUSTOMER file and DB2 access patterns from:
 *   - INQCUST.cbl (READ by sortCode + customerNumber)
 *   - CRECUST.cbl (WRITE new customer)
 *   - UPDCUST.cbl (REWRITE customer)
 *   - DELCUS.cbl (DELETE customer)
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findBySortCodeAndCustomerNumber(String sortCode, String customerNumber);

    Optional<Customer> findByCustomerNumber(String customerNumber);

    void deleteBySortCodeAndCustomerNumber(String sortCode, String customerNumber);

    Optional<Customer> findTopBySortCodeOrderByCustomerNumberDesc(String sortCode);

    long countBySortCode(String sortCode);
}

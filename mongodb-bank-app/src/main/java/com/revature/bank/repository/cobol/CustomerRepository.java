package com.revature.bank.repository.cobol;

import com.revature.bank.model.cobol.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByCustomerNumber(String customerNumber);

    Optional<Customer> findBySortCodeAndCustomerNumber(String sortCode, String customerNumber);

    long countBySortCode(String sortCode);
}

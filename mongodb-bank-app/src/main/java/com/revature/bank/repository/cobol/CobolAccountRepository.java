package com.revature.bank.repository.cobol;

import com.revature.bank.model.cobol.CobolAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CobolAccountRepository extends MongoRepository<CobolAccount, String> {

    Optional<CobolAccount> findBySortCodeAndAccountNumber(String sortCode, String accountNumber);

    List<CobolAccount> findByCustomerNumber(String customerNumber);

    long countByCustomerNumber(String customerNumber);
}

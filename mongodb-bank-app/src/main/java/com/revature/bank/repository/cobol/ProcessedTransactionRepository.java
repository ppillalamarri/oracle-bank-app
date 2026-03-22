package com.revature.bank.repository.cobol;

import com.revature.bank.model.cobol.ProcessedTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessedTransactionRepository extends MongoRepository<ProcessedTransaction, String> {

    List<ProcessedTransaction> findBySortCodeAndAccountNumberOrderByTransactionDateDescTransactionTimeDesc(
            String sortCode, String accountNumber);
}

package com.bankapp.repository;

import com.bankapp.model.ProcessedTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for ProcessedTransaction documents.
 *
 * Replaces COBOL DB2 PROCTRAN table access patterns from:
 *   - DBCRFUN.cbl (INSERT after debit/credit)
 *   - XFRFUN.cbl (INSERT after transfer)
 *   - CREACC.cbl (INSERT after account creation)
 *   - CRECUST.cbl (INSERT after customer creation)
 */
@Repository
public interface ProcessedTransactionRepository extends MongoRepository<ProcessedTransaction, String> {

    List<ProcessedTransaction> findBySortCodeAndAccountNumber(String sortCode, String accountNumber);

    List<ProcessedTransaction> findBySortCodeAndAccountNumberOrderByTransactionDateDescTransactionTimeDesc(
            String sortCode, String accountNumber);

    List<ProcessedTransaction> findByTransactionType(String transactionType);
}

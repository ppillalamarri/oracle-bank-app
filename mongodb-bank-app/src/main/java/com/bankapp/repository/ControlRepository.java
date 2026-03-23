package com.bankapp.repository;

import com.bankapp.model.Control;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for Control documents.
 *
 * Replaces COBOL Named Counter Server (NCS) and DB2 CONTROL table access from:
 *   - CREACC.cbl (ENQ/DEQ named counter for account numbers)
 *   - CRECUST.cbl (ENQ/DEQ named counter for customer numbers)
 *   - CONTDB2.cpy (DB2 CONTROL table declaration)
 */
@Repository
public interface ControlRepository extends MongoRepository<Control, String> {

    Optional<Control> findByControlName(String controlName);
}

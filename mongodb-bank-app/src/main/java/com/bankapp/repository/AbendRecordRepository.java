package com.bankapp.repository;

import com.bankapp.model.AbendRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * MongoDB repository for AbendRecord documents.
 *
 * Replaces COBOL ABNDPROC.cbl VSAM ABNDFILE KSDS access:
 *   - WRITE to ABNDFILE (persisting abend records)
 *   - READ from ABNDFILE (querying abend history)
 */
@Repository
public interface AbendRecordRepository extends MongoRepository<AbendRecord, String> {

    List<AbendRecord> findByProgram(String program);

    List<AbendRecord> findByAbendCode(String abendCode);

    List<AbendRecord> findByAbendDateBetween(LocalDate startDate, LocalDate endDate);

    List<AbendRecord> findByAbendDateOrderByAbendTimeDesc(LocalDate date);
}

package com.revature.bank.repository.cobol;

import com.revature.bank.model.cobol.AbendRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbendRecordRepository extends MongoRepository<AbendRecord, String> {

    Optional<AbendRecord> findByUtimeKeyAndTaskNumber(long utimeKey, String taskNumber);
}

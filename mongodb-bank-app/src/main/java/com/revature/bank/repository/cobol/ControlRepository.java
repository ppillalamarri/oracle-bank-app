package com.revature.bank.repository.cobol;

import com.revature.bank.model.cobol.Control;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ControlRepository extends MongoRepository<Control, String> {

    Optional<Control> findByControlName(String controlName);
}

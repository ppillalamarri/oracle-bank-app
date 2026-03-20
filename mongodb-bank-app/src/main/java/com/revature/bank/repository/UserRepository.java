package com.revature.bank.repository;

import com.revature.bank.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for User documents.
 * Replaces the Oracle-based UserDao and AccountDao interfaces.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);

    List<User> findByType(String type);

    @Query("{'accounts.approved': 'pending'}")
    List<User> findUsersWithPendingAccounts();

    @Query("{'accounts.accountId': ?0}")
    Optional<User> findByAccountId(String accountId);
}

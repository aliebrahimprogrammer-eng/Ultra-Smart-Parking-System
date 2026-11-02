package com.ga.parking.repository;


import com.ga.parking.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>  {
    // For Registration
    boolean existsByEmailAddress(String emailAddress);

    // For Login
    User findUserByEmailAddress(String emailAddress);
}

package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.User;

public interface UserRepository extends CrudRepository<User, String> {
    User findByEmail(String email);
    
    @Query(value="{ 'email': ?0 }", fields="{ 'email': 1, '_id': 0 }")
    User findEmailByEmail(String email);
}

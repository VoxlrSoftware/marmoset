package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.User;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(String username);
    
    @Query(value="{ 'username': ?0 }", fields="{ 'username': 1, '_id': 0 }")
    User findUsernameByUsername(String username);
}

package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;

import com.voxlr.marmoset.model.persistence.User;

public interface UserRepository extends EntityRepository<User> {
    User findByEmail(String email);
    
    @Query(value="{ 'email': ?0 }", fields="{ 'email': 1, '_id': 0 }")
    User findEmailByEmail(String email);
}

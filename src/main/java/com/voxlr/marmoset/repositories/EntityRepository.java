package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.persistence.Entity;

public interface EntityRepository<T extends Entity> extends CrudRepository<T, String> {
    @Query(value="{ '_id': ?0 }", fields="{ '_id': 1 }")
    T findIdById(String id);
}

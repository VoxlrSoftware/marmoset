package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.persistence.Team;

public interface TeamRepository extends CrudRepository<Team, String> {
    @Query(value="{ '_id': ?0 }", fields="{ '_id': 1 }")
    Team findIdById(String id);
}

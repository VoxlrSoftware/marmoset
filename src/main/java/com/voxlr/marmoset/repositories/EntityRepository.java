package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.model.persistence.Entity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EntityRepository<T extends Entity> extends CrudRepository<T, String> {
  @Query(value = "{ '_id': ?0 }", fields = "{ '_id': 1 }")
  T findIdById(String id);
}

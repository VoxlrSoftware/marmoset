package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.model.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends EntityRepository<User>, CustomizedUserRepository {
  @Query(value = "{'isInactive': false, 'email': ?0}")
  User findByEmail(String email);

  @Query(value = "{'isInactive': false, 'email': ?0 }", fields = "{ 'email': 1, 'id': 0 }")
  User findEmailByEmail(String email);

  @Query(value = "{'id': ?0}", fields = "{'companyId': 1, 'teamId': 1}")
  User getAssociationForUser(String id);

  @Query(value = "{'isInactive': false, 'companyId': ?0}")
  Page<User> findAllByCompany(String companyId, Pageable pageable);
}

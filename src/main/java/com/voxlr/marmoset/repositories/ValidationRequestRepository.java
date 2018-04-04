package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.model.persistence.ValidationRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

public interface ValidationRequestRepository extends EntityRepository<ValidationRequest> {

  @Query(value = "{'userId': ?0, 'entityId': ?0}")
  ValidationRequest locateRequest(ObjectId userId, ObjectId entityId);

  ValidationRequest findOneByRequestId(String requestId);
}

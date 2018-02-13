package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.repository.Query;

import com.voxlr.marmoset.model.persistence.ValidationRequest;

public interface ValidationRequestRepository extends EntityRepository<ValidationRequest> {

    @Query(value="{'userId': ?0, 'entityId': ?0}")
    ValidationRequest locateRequest(String userId, String entityId);
    
    ValidationRequest findOneByRequestId(String requestId);
}

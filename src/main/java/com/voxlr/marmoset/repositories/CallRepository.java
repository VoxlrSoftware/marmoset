package com.voxlr.marmoset.repositories;

import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.persistence.Call;

public interface CallRepository extends CrudRepository<Call, String> {
    Call findOneByCallSid(String callSid);
}

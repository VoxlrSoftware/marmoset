package com.voxlr.marmoset.repositories;

import java.util.Optional;

import com.voxlr.marmoset.model.persistence.Call;

public interface CallRepository extends EntityRepository<Call>, AtomicUpdate<Call>, CustomizedCallRepository {
    Optional<Call> findOneByCallSid(String callSid);
    Optional<Call> findOneByTranscriptionId(String transcriptionId);
}

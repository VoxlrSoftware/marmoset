package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.model.persistence.Call;

import java.util.Optional;

public interface CallRepository
    extends EntityRepository<Call>, AtomicUpdate<Call>, CustomizedCallRepository {
  Optional<Call> findOneByCallSid(String callSid);

  Optional<Call> findOneByTranscriptionId(String transcriptionId);
}

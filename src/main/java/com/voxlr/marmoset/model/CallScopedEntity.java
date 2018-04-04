package com.voxlr.marmoset.model;

import org.bson.types.ObjectId;

public interface CallScopedEntity {
  ObjectId getCallId();
}

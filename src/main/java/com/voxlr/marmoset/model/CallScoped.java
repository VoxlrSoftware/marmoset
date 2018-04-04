package com.voxlr.marmoset.model;

import org.bson.types.ObjectId;

public interface CallScoped {
  ObjectId getId();

  String getCallSid();
}

package com.voxlr.marmoset.model;

import org.bson.types.ObjectId;

public interface UserScopedEntity {
  ObjectId getUserId();
}

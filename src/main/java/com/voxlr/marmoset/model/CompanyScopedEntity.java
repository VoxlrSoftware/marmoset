package com.voxlr.marmoset.model;

import org.bson.types.ObjectId;

public interface CompanyScopedEntity extends GlobalEntity {
  ObjectId getCompanyId();
}

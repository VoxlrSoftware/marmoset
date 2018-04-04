package com.voxlr.marmoset.model;

import org.bson.types.ObjectId;

public interface TeamScopedEntity extends CompanyScopedEntity {
  ObjectId getTeamId();
}

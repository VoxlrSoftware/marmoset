package com.voxlr.marmoset.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class RemovedEntityDTO {
  private ObjectId id;
  private boolean deleted = true;

  public RemovedEntityDTO(ObjectId id) {
    this.id = id;
  }
}

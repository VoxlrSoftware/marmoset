package com.voxlr.marmoset.model;

import org.springframework.data.mongodb.core.query.Update;

public class EntityUpdateHolder {
  private Update update;

  public Update getUpdate() {
    if (update == null) {
      update = new Update();
    }

    return update;
  }

  public boolean isUpdateRequired() {
    return update != null;
  }
}

package com.voxlr.marmoset.model.persistence;

import lombok.*;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallStrategy extends AuditModel {
  private String name;

  private List<String> phrases = new ArrayList<String>();

  public CallStrategy update(String name, List<String> phrases) {
    if (name != null) {
      this.name = name;
    }

    if (phrases != null) {
      this.phrases = phrases;
    }

    this.setLastModified(new DateTime());
    return this;
  }

  public static CallStrategy createNew() {
    CallStrategy callStrategy = new CallStrategy();
    callStrategy.setId(new ObjectId());
    callStrategy.setCreateDate(new DateTime());
    return callStrategy;
  }
}

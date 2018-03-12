package com.voxlr.marmoset.model.persistence;

import com.voxlr.marmoset.model.GlobalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;

@NoArgsConstructor
@Getter
@Setter
public abstract class Entity implements GlobalEntity {

  @Id private String id;

  public void onPersistenceSave(MongoTemplate mongoTemplate) {}
}

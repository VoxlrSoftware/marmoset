package com.voxlr.marmoset.model.persistence;

import com.voxlr.marmoset.model.GlobalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;

@NoArgsConstructor
@Getter
@Setter
public abstract class Entity implements GlobalEntity {

  @Id private ObjectId id;

  public void onPersistenceSave(MongoTemplate mongoTemplate) {}
}

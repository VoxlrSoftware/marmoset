package com.voxlr.marmoset.model.persistence.lifecycle;

import com.voxlr.marmoset.model.persistence.Entity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

public class EntityLifeCycle extends AbstractLifeCycle<Entity> {

  @Override
  void beforeConvert(BeforeConvertEvent<Entity> event, MongoTemplate mongoTemplate) {}

  @Override
  void beforeSave(BeforeSaveEvent<Entity> event, MongoTemplate mongoTemplate) {
    Entity entity = event.getSource();
    entity.onPersistenceSave(mongoTemplate);
  }

  @Override
  void beforeDelete(BeforeDeleteEvent<Entity> event, MongoTemplate mongoTemplate) {}

  @Override
  void afterConvert(AfterConvertEvent<Entity> event, MongoTemplate mongoTemplate) {}
}

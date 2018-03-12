package com.voxlr.marmoset.model.persistence.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractLifeCycle<T> extends AbstractMongoEventListener<T> {
  @Autowired private MongoTemplate mongoTemplate;

  @Override
  public void onBeforeConvert(BeforeConvertEvent<T> event) {
    beforeConvert(event, mongoTemplate);
  }

  @Override
  public void onBeforeDelete(BeforeDeleteEvent<T> event) {
    beforeDelete(event, mongoTemplate);
  }

  @Override
  public void onBeforeSave(BeforeSaveEvent<T> event) {
    beforeSave(event, mongoTemplate);
  }

  @Override
  public void onAfterConvert(AfterConvertEvent<T> event) {
    afterConvert(event, mongoTemplate);
  }

  abstract void beforeConvert(BeforeConvertEvent<T> event, MongoTemplate mongoTemplate);

  abstract void beforeSave(BeforeSaveEvent<T> event, MongoTemplate mongoTemplate);

  abstract void beforeDelete(BeforeDeleteEvent<T> event, MongoTemplate mongoTemplate);

  abstract void afterConvert(AfterConvertEvent<T> event, MongoTemplate mongoTemplate);
}

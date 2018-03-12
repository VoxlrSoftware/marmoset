package com.voxlr.marmoset.util;

import com.voxlr.marmoset.model.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.Set;

public class PersistenceUtils {

  @Autowired private MongoTemplate mongoTemplate;

  private Set<Class<?>> touchedClasses = new HashSet<>();

  public void save(Entity... entities) {
    for (int i = 0; i < entities.length; i++) {
      Entity entity = entities[i];
      mongoTemplate.save(entity);
      updateEntity(entity);
      touchedClasses.add(entity.getClass());
    }
  }

  public void cleanup() {
    touchedClasses
        .stream()
        .forEach(
            entityClass -> {
              removeAll(entityClass);
            });
  }

  public void removeAll(String collectionName) {
    mongoTemplate.findAllAndRemove(new Query(), collectionName);
  }

  @SuppressWarnings("unchecked")
  public void removeAll(Class className) {
    mongoTemplate.findAllAndRemove(new Query(), className);
  }

  public <T extends Entity> void removeAllAndSave(Class<T> className, T... entities) {
    removeAll(className);
    save(entities);
  }

  public void updateEntity(Entity entity) {
    entity = mongoTemplate.findById(entity.getId(), entity.getClass());
  }
}

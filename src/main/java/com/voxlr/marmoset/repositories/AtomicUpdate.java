package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.model.persistence.Entity;
import com.voxlr.marmoset.model.persistence.factory.EntityUpdate;
import org.springframework.data.mongodb.core.query.Update;

public interface AtomicUpdate<T extends Entity> {
  T update(EntityUpdate<T> update);

  T update(T entity, Update update);
}

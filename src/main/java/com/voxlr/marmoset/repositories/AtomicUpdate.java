package com.voxlr.marmoset.repositories;

import org.springframework.data.mongodb.core.query.Update;

import com.voxlr.marmoset.model.persistence.Entity;
import com.voxlr.marmoset.model.persistence.factory.EntityUpdate;

public interface AtomicUpdate<T extends Entity> {
    T update(EntityUpdate<T> update);
    T update(T entity, Update update);
}

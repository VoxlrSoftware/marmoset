package com.voxlr.marmoset.model.persistence.lifecycle;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import com.voxlr.marmoset.model.persistence.Call;

public class CallLifecycle extends AbstractLifeCycle<Call> {

    @Override
    void beforeConvert(BeforeConvertEvent<Call> event, MongoTemplate mongoTemplate) {
	
    }

    @Override
    void beforeSave(BeforeSaveEvent<Call> event, MongoTemplate mongoTemplate) {

    }

    @Override
    void beforeDelete(BeforeDeleteEvent<Call> event, MongoTemplate mongoTemplate) {

    }

    @Override
    void afterConvert(AfterConvertEvent<Call> event, MongoTemplate mongoTemplate) {

    }
}

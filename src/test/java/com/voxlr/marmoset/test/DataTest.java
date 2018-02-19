package com.voxlr.marmoset.test;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.voxlr.marmoset.config.PersistenceTestConfig;
import com.voxlr.marmoset.util.PersistenceUtils;

@RunWith(SpringRunner.class)
@DataMongoTest
@ContextConfiguration(classes = PersistenceTestConfig.class)
public abstract class DataTest extends IntegrationTest {

    @Autowired
    protected MongoTemplate mongoTemplate;
    
    @Autowired
    protected PersistenceUtils persistenceUtils;
    
    @Before
    public void before() {
	this.beforeTest();
    }
    
    public void beforeTest() {}
    
    @After
    public void after() {
	afterTest();
	persistenceUtils.cleanup();
    }
    
    public void afterTest() {}
}
	
package com.voxlr.marmoset.test;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;
import com.voxlr.marmoset.config.PersistenceTestConfig;
import com.voxlr.marmoset.util.PersistenceUtils;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@DataMongoTest
@WithModelMapper(basePackages = "com.voxlr.marmoset")
@ActiveProfiles("dev")
@ContextConfiguration(classes = PersistenceTestConfig.class)
public abstract class IntegrationTest {
    
    @Autowired
    protected ModelMapper modelMapper;
    
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
	mongoTemplate.getCollectionNames().stream().forEach(x -> {
	    mongoTemplate.findAllAndRemove(new Query(), x);
	});
    }
    
    public void afterTest() {}
}

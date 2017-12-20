package com.voxlr.marmoset.test;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WithModelMapper
public abstract class IntegrationTest {
    
    @Autowired
    protected ModelMapper modelMapper;
}

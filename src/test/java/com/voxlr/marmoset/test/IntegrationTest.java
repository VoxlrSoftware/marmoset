package com.voxlr.marmoset.test;

import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;
import org.junit.experimental.categories.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Category(IntegrationTest.class)
@WithModelMapper(basePackages = "com.voxlr.marmoset")
@ActiveProfiles("test")
public abstract class IntegrationTest {

  @Autowired protected ModelMapper modelMapper;
}

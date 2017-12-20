package com.voxlr.marmoset.test;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;

@Category(UnitTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WithModelMapper
public abstract class UnitTest {

}

package com.voxlr.marmoset.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class AssertUtils {

    public static void wrapNoException(GenericLambda lamdba) {
	try {
	    lamdba.apply();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
    
    public static <T extends Class> void wrapAssertException(GenericLambda lambda, T expectedClass) {
	try {
	    lambda.apply();
	    fail();
	} catch (Exception e) {
	    assertThat(e, instanceOf(expectedClass));
	}
    }
    
    public static <T> Matcher<List<T>> containsMatch(Function<T, Boolean> expression) {
	return new TypeSafeMatcher<List<T>>() {

	    @Override
	    public void describeTo(Description description) {
		description.appendText("list should contain matching expression");
		
	    }

	    @Override
	    protected boolean matchesSafely(List<T> itemList) {
		return itemList.stream().filter(item -> expression.apply(item)).findFirst().isPresent();
	    }
	};
    }
}

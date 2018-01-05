package com.voxlr.marmoset.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
}

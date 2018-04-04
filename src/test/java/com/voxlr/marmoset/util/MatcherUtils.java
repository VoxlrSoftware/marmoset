package com.voxlr.marmoset.util;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import org.bson.types.ObjectId;
import org.mockito.internal.matchers.InstanceOf;

public class MatcherUtils {

  public static ObjectId anyObjectId() {
    mockingProgress().getArgumentMatcherStorage().reportMatcher((new InstanceOf(ObjectId.class, "<any ObjectId>")));
    return new ObjectId();
  }

}

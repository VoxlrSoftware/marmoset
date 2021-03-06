package com.voxlr.marmoset.util.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.json.JsonObject;

public class ContainsKeyMatcher extends TypeSafeMatcher<JsonObject> {
  private String expected;

  private ContainsKeyMatcher(String expected) {
    this.expected = expected;
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue("JSONObject contains key");
  }

  @Override
  protected boolean matchesSafely(JsonObject item) {
    return item.containsKey(expected);
  }

  public static ContainsKeyMatcher containsKey(String expected) {
    return new ContainsKeyMatcher(expected);
  }
}

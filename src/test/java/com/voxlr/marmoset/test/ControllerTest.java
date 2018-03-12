package com.voxlr.marmoset.test;

import org.json.JSONException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public abstract class ControllerTest extends IntegrationTest {

  protected void validateStatus(MvcResult result, HttpStatus status) {
    assertEquals(result.getResponse().getStatus(), status.value());
  }

  protected void validateResponse(MvcResult result, String expected)
      throws UnsupportedEncodingException, JSONException {
    JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
  }
}

package com.voxlr.marmoset.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
public class ControllerTest extends IntegrationTest {

    protected void validateStatus(MvcResult result, HttpStatus status) {
	assertEquals(result.getResponse().getStatus(), status.value());
    }
    
    protected void validateResponse(MvcResult result, String expected) throws UnsupportedEncodingException, JSONException {
	JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }
}

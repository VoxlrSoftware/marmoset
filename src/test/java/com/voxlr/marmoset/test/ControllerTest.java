package com.voxlr.marmoset.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class ControllerTest extends IntegrationTest {
    
    @Autowired
    protected ModelMapper modelMapper;

    protected void validateStatus(MvcResult result, HttpStatus status) {
	assertEquals(result.getResponse().getStatus(), status.value());
    }
    
    protected void validateResponse(MvcResult result, String expected) throws UnsupportedEncodingException, JSONException {
	JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }
}

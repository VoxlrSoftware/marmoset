package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.json.ContainsKeyMatcher.containsKey;
import static com.voxlr.marmoset.util.json.JsonUtils.jsonFromString;
import static javax.json.Json.createObjectBuilder;
import static javax.json.Json.createReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.ws.Response;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.service.CompanyService;
import com.voxlr.marmoset.test.IntegrationTest;
import com.voxlr.marmoset.util.error.ApiError;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(secure=false)
@WithModelMapper
public class CompanyControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private CompanyService companyService;
    
    @MockBean
    private CompanyRepository companyRepository;
    
    @MockBean AuthUser authUser;
    
    @Autowired
    private ModelMapper modelMapper;
    
    ObjectMapper mapper = new ObjectMapper();
    Company mockCompany = createCompany("Test Company", "Random phrase");
    String expected;
    
    @Before
    public void setup() throws JsonProcessingException {
	expected = mapper.writeValueAsString(modelMapper.map(mockCompany, CompanyDTO.class));
    }
    
    private void validateStatus(MvcResult result, HttpStatus status) {
	assertEquals(result.getResponse().getStatus(), status.value());
    }
    
    private void validateResponse(MvcResult result, String expected) throws UnsupportedEncodingException, JSONException {
	JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }
    
    @Test
    public void getShouldReturnValidCompany() throws Exception {
	when(companyService.get(anyString(), any(AuthUser.class)))
		.thenReturn(mockCompany);
	
	RequestBuilder requestBuilder = get(
		"/api/company/" + mockCompany.getId()).accept(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    @Test
    public void postShouldReturnValidCompany() throws Exception {
	when(companyService.create(any(CompanyCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockCompany);
	
	String body = createObjectBuilder()
			.add("name", "Test Company").build().toString();
	
	RequestBuilder requestBuilder = post("/api/company")
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON)
		.content(body);
	MvcResult result = mvc.perform(requestBuilder).andReturn();

	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    @Test
    public void postWithInvalidBodyShouldThrowException() throws Exception {
	RequestBuilder requestBuilder = post("/api/company")
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.BAD_REQUEST);
	
	
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
    }

}

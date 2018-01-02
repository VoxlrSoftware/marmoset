package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.util.EntityTestUtils.createAuditableEntity;
import static com.voxlr.marmoset.util.json.ContainsKeyMatcher.containsKey;
import static com.voxlr.marmoset.util.json.JsonUtils.jsonFromString;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.UUID;

import javax.json.JsonObject;
import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallDTO;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.service.CallService;
import com.voxlr.marmoset.test.ControllerTest;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidator;

@WebMvcTest(CallController.class)
@AutoConfigureMockMvc(secure = false)
public class CallControllerTest extends ControllerTest {

   @Autowired
   private MockMvc mvc;
   
   @Mock
   private PhoneNumberValidator phoneNumberValidator;
   
   @MockBean
   private CallRepository callRepository;
   
   @MockBean
   private CallService callService;
   
   ObjectMapper mapper = new ObjectMapper();
   Call mockCall;
   String expected;
   
   @Before
   public void setup() throws JsonProcessingException {
       mockCall = createAuditableEntity(
	       Call.builder()
	       .callSid(UUID.randomUUID().toString())
	       .employeeNumber("+19099446352")
	       .customerNumber("+19099446352")
	       .companyId("123")
	       .strategy("How are you?")
	       .userId("123")
	       .build());
       expected = mapper.writeValueAsString(modelMapper.map(mockCall, CallDTO.class));
   }
   
   @Test
   public void getShouldReturnExceptionIfEntityDoesNotExist() throws Exception {
	when(callService.get(anyString(), any(AuthUser.class)))
	.thenAnswer(new Answer<Call>() {

	    @Override
	    public Call answer(InvocationOnMock invocation) throws Throwable {
		throw new EntityNotFoundException(Call.class, "id", (String)invocation.getArguments()[0]);
	    }
	});
	
	String fakeId = "123";
	RequestBuilder requestBuilder = get(
		"/api/call/" + fakeId).accept(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	validateStatus(result, HttpStatus.NOT_FOUND);
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Call was not found for parameters {id=" + fakeId + "}"));
   }
   
   @Test
   public void getShouldReturnValidCall() throws Exception {
	when(callService.get(eq(mockCall.getId()), any(AuthUser.class)))
		.thenReturn(mockCall);
	
	RequestBuilder requestBuilder = get(
		"/api/call/" + mockCall.getId()).accept(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
   }
   
   @Test
   public void postShouldReturnValidCall() throws Exception {
       when(callService.create(any(CallCreateDTO.class), any(AuthUser.class)))
       	.thenReturn(mockCall);
       when(phoneNumberValidator.isValid(anyString(), any(ConstraintValidatorContext.class)))
       	.thenReturn(true);
       
       MvcResult result = doPostWithCall();
       verify(callService, times(1)).create(any(CallCreateDTO.class), any(AuthUser.class));
       validateStatus(result, HttpStatus.OK);
       validateResponse(result, expected);
   }
   
   private MvcResult doPostWithCall() throws Exception {
	String body = createObjectBuilder()
		.add("callSid", mockCall.getCallSid())
		.add("employeeNumber", mockCall.getEmployeeNumber())
		.add("customerNumber", mockCall.getCustomerNumber())
		.add("strategyList", createArrayBuilder()
			.add("This is a phrase").build())
		.build().toString();
	
	RequestBuilder requestBuilder = post("/api/call")
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON)
		.content(body);
	return mvc.perform(requestBuilder).andReturn();
   }
}

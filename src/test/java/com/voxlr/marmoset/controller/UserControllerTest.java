package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.util.EntityTestUtils.createAuditableEntity;
import static com.voxlr.marmoset.util.json.ContainsKeyMatcher.containsKey;
import static com.voxlr.marmoset.util.json.JsonUtils.jsonFromString;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.RemovedEntityDTO;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.service.CompanyService;
import com.voxlr.marmoset.service.TeamService;
import com.voxlr.marmoset.service.UserService;
import com.voxlr.marmoset.test.ControllerTest;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(secure = false)
public class UserControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private CompanyService companyService;
    
    @MockBean
    private TeamService teamService;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private AuthUser authUser;
    
    ObjectMapper mapper = new ObjectMapper();
    User mockUser;
    String expected;
    
    @Before
    public void setup() throws JsonProcessingException {
	mockUser = createAuditableEntity(
		User.builder().firstName("First")
			.lastName("Last")
			.companyId("123")
			.teamId("345")
			.email("first.last@email.com")
			.build()
		);
	expected = mapper.writeValueAsString(modelMapper.map(mockUser, UserDTO.class));
    }
    
    @Test
    public void getShouldReturnExceptionIfEntityDoesNotExist() throws Exception {
 	when(userService.get(anyString(), any(AuthUser.class)))
 	.thenAnswer(new Answer<User>() {

 	    @Override
 	    public User answer(InvocationOnMock invocation) throws Throwable {
 		throw new EntityNotFoundException(User.class, "id", (String)invocation.getArguments()[0]);
 	    }
 	});
 	
 	String fakeId = "123";
 	RequestBuilder requestBuilder = get(
 		"/api/user/" + fakeId).accept(APPLICATION_JSON);
 	MvcResult result = mvc.perform(requestBuilder).andReturn();
 	validateStatus(result, HttpStatus.NOT_FOUND);
 	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
 	assertThat(response, containsKey("apierror"));
 	
 	JsonObject error = response.getJsonObject("apierror");
 	assertThat(error.getString("message"), is("User was not found for parameters {id=" + fakeId + "}"));
    }
    
    @Test
    public void getShouldReturnValidUser() throws Exception {
	when(userService.get(eq(mockUser.getId()), any(AuthUser.class)))
	.thenReturn(mockUser);
	
	RequestBuilder requestBuilder = get(
		"/api/user/" + mockUser.getId()).accept(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    @Test
    public void postShouldReturnValidUser() throws Exception {
	when(userService.create(any(UserCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockUser);
	when(userService.validateUniqueEmail(anyString())).thenReturn(true);
	when(companyService.validateExists(anyString())).thenReturn(true);
	when(teamService.validateExists(anyString())).thenReturn(true);
	
	MvcResult result = doPostWithUser();

	verify(userService, times(1)).create(any(UserCreateDTO.class), any(AuthUser.class));
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    @Test
    public void postShouldReturnErrorWhenEmailIsNotUnique() throws Exception {
	when(userService.create(any(UserCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockUser);
	when(userService.validateUniqueEmail(anyString())).thenReturn(false);
	when(companyService.validateExists(anyString())).thenReturn(true);
	when(teamService.validateExists(anyString())).thenReturn(true);
	
	MvcResult result = doPostWithUser();

	validateStatus(result, HttpStatus.BAD_REQUEST);
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Validation error"));
	assertThat(error.getJsonArray("subErrors").getJsonObject(0).getString("field"),	is("email"));
    }
    
    @Test
    public void postShouldReturnErrorWhenCompanyDoesNotExist() throws Exception {
	when(userService.create(any(UserCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockUser);
	when(userService.validateUniqueEmail(anyString())).thenReturn(true);
	when(companyService.validateExists(anyString())).thenReturn(false);
	when(teamService.validateExists(anyString())).thenReturn(true);
	
	MvcResult result = doPostWithUser();

	validateStatus(result, HttpStatus.BAD_REQUEST);
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Validation error"));
	assertThat(error.getJsonArray("subErrors").getJsonObject(0).getString("field"),	is("companyId"));
    }
    
    @Test
    public void postShouldReturnErrorWhenTeamDoesNotExist() throws Exception {
	when(userService.create(any(UserCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockUser);
	when(userService.validateUniqueEmail(anyString())).thenReturn(true);
	when(companyService.validateExists(anyString())).thenReturn(true);
	when(teamService.validateExists(anyString())).thenReturn(false);
	
	MvcResult result = doPostWithUser();

	validateStatus(result, HttpStatus.BAD_REQUEST);
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Validation error"));
	assertThat(error.getJsonArray("subErrors").getJsonObject(0).getString("field"),	is("teamId"));
    }
    
    @Test
    public void postWithInvalidBodyShouldReturnException() throws Exception {
	RequestBuilder requestBuilder = post("/api/user")
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.BAD_REQUEST);
	
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Malformed JSON request"));
    }
    
    @Test
    public void putShouldReturnValidUser() throws Exception {
	when(userService.update(any(UserUpdateDTO.class), any(AuthUser.class)))
	.thenReturn(mockUser);
	
	String body = createObjectBuilder()
		.add("name", "New Name").build().toString();
	RequestBuilder requestBuilder = put("/api/user/" + mockUser.getId())
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON)
		.content(body);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	verify(userService, times(1)).update(any(UserUpdateDTO.class), any(AuthUser.class));
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    @Test
    public void putWithInvalidBodyShouldReturnException() throws Exception {
	RequestBuilder requestBuilder = put("/api/user/" + mockUser.getId())
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	validateStatus(result, HttpStatus.BAD_REQUEST);
	
	JsonObject response = jsonFromString(result.getResponse().getContentAsString());
	assertThat(response, containsKey("apierror"));
	JsonObject error = response.getJsonObject("apierror");
	assertThat(error.getString("message"), is("Malformed JSON request"));
    }
    
    @Test
    public void deleteShouldReturnValidResponse() throws Exception {
	RequestBuilder requestBuilder = delete("/api/user/" + mockUser.getId())
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	RemovedEntityDTO responseDTO = new RemovedEntityDTO(mockUser.getId());
	String expected = mapper.writeValueAsString(responseDTO);
	validateStatus(result, HttpStatus.OK);
	validateResponse(result, expected);
    }
    
    private MvcResult doPostWithUser() throws Exception {
	String body = createObjectBuilder()
		.add("companyId", mockUser.getCompanyId())
		.add("teamId", mockUser.getTeamId())
		.add("firstName", mockUser.getFirstName())
		.add("lastName", mockUser.getLastName())
		.add("password", "Password")
		.add("email", mockUser.getEmail()).build().toString();
	
	RequestBuilder requestBuilder = post("/api/user")
		.accept(APPLICATION_JSON)
		.contentType(APPLICATION_JSON)
		.content(body);
	return mvc.perform(requestBuilder).andReturn();
    }

}

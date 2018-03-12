package com.voxlr.marmoset.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.dto.RemovedEntityDTO;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.service.domain.CompanyService;
import com.voxlr.marmoset.test.ControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.json.JsonObject;

import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.JsonUtils.jsonFromString;
import static com.voxlr.marmoset.util.matcher.ContainsKeyMatcher.containsKey;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc(secure = false)
@WebMvcTest(CompanyController.class)
public class CompanyControllerTest extends ControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CompanyService companyService;

  @MockBean private CompanyRepository companyRepository;

  @MockBean private AuthUser authUser;

  ObjectMapper mapper = new ObjectMapper();
  Company mockCompany =
      createCompany("Test Company", "Random phrase")
          .setPhoneNumber(new PhoneNumberHolder("+19099446352"));
  String expected;

  @Before
  public void setup() throws JsonProcessingException {
    expected = mapper.writeValueAsString(modelMapper.map(mockCompany, CompanyDTO.class));
  }

  @Test
  public void getShouldReturnValidCompany() throws Exception {
    when(companyService.get(anyString(), any(AuthUser.class))).thenReturn(mockCompany);

    RequestBuilder requestBuilder =
        get("/api/company/" + mockCompany.getId()).accept(APPLICATION_JSON);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    validateStatus(result, HttpStatus.OK);
    validateResponse(result, expected);
  }

  @Test
  public void postShouldReturnValidCompany() throws Exception {
    when(companyService.create(any(CompanyCreateDTO.class), any(AuthUser.class)))
        .thenReturn(mockCompany);

    String body =
        createObjectBuilder()
            .add("name", "Test Company")
            .add("phoneNumber", createObjectBuilder().add("number", "+19099446352").build())
            .build()
            .toString();

    RequestBuilder requestBuilder =
        post("/api/company").accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(body);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    verify(companyService, times(1)).create(any(CompanyCreateDTO.class), any(AuthUser.class));
    validateStatus(result, HttpStatus.OK);
    validateResponse(result, expected);
  }

  @Test
  public void postWithInvalidPhoneNumberShouldReturnException() throws Exception {
    when(companyService.create(any(CompanyCreateDTO.class), any(AuthUser.class)))
        .thenReturn(mockCompany);

    String body =
        createObjectBuilder()
            .add("name", "Test Company")
            .add("phoneNumber", createObjectBuilder().add("number", "+11234567890").build())
            .build()
            .toString();

    RequestBuilder requestBuilder =
        post("/api/company").accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(body);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    validateStatus(result, HttpStatus.BAD_REQUEST);
    JsonObject response = jsonFromString(result.getResponse().getContentAsString());
    assertThat(response, containsKey("apierror"));

    JsonObject error = response.getJsonObject("apierror");
    assertThat(error.getString("message"), is("Validation error"));
    assertThat(
        error.getJsonArray("subErrors").getJsonObject(0).getString("field"), is("phoneNumber"));
  }

  @Test
  public void postWithInvalidBodyShouldReturnException() throws Exception {
    RequestBuilder requestBuilder =
        post("/api/company").accept(APPLICATION_JSON).contentType(APPLICATION_JSON);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    validateStatus(result, HttpStatus.BAD_REQUEST);

    JsonObject response = jsonFromString(result.getResponse().getContentAsString());
    assertThat(response, containsKey("apierror"));
    JsonObject error = response.getJsonObject("apierror");
    assertThat(error.getString("message"), is("Malformed JSON request"));
  }

  @Test
  public void putShouldReturnValidCompany() throws Exception {
    when(companyService.update(any(CompanyUpdateDTO.class), any(AuthUser.class)))
        .thenReturn(mockCompany);

    String body =
        createObjectBuilder()
            .add("name", "Test Company")
            .add("phoneNumber", createObjectBuilder().add("number", "+19099446352").build())
            .build()
            .toString();
    RequestBuilder requestBuilder =
        put("/api/company/" + mockCompany.getId())
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(body);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    verify(companyService, times(1)).update(any(CompanyUpdateDTO.class), any(AuthUser.class));
    validateStatus(result, HttpStatus.OK);
    validateResponse(result, expected);
  }

  @Test
  public void putWithInvalidPhoneNumberShouldReturnException() throws Exception {
    String body =
        createObjectBuilder()
            .add("phoneNumber", createObjectBuilder().add("number", "+11234567890").build())
            .build()
            .toString();
    RequestBuilder requestBuilder =
        put("/api/company/" + mockCompany.getId())
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(body);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    validateStatus(result, HttpStatus.BAD_REQUEST);
    JsonObject response = jsonFromString(result.getResponse().getContentAsString());
    assertThat(response, containsKey("apierror"));

    JsonObject error = response.getJsonObject("apierror");
    assertThat(error.getString("message"), is("Validation error"));
    assertThat(
        error.getJsonArray("subErrors").getJsonObject(0).getString("field"), is("phoneNumber"));
  }

  @Test
  public void putWithInvalidBodyShouldReturnException() throws Exception {
    RequestBuilder requestBuilder =
        put("/api/company/" + mockCompany.getId())
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
    RequestBuilder requestBuilder =
        delete("/api/company/" + mockCompany.getId())
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON);
    MvcResult result = mvc.perform(requestBuilder).andReturn();

    RemovedEntityDTO responseDTO = new RemovedEntityDTO(mockCompany.getId());
    String expected = mapper.writeValueAsString(responseDTO);
    validateStatus(result, HttpStatus.OK);
    validateResponse(result, expected);
  }
}

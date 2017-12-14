package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rozidan.springboot.modelmapper.testing.WithModelMapper;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyDTO;
import com.voxlr.marmoset.service.CompanyService;
import com.voxlr.marmoset.test.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(secure=false)
@WithModelMapper
public class CompanyControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private CompanyService companyService;
    
    @MockBean AuthUser authUser;
    
    @Autowired
    private ModelMapper modelMapper;
    
    ObjectMapper mapper = new ObjectMapper();
    Company mockCompany = createCompany("Test Company", "Random phrase");
    
    @Test
    public void getShouldReturnValidCompany() throws Exception {
	when(companyService.get(anyString(), any(AuthUser.class)))
		.thenReturn(mockCompany);
	
	RequestBuilder requestBuilder = get(
		"/api/company/" + mockCompany.getId()).accept(APPLICATION_JSON);
	MvcResult result = mvc.perform(requestBuilder).andReturn();
	
	CompanyDTO companyDTO = modelMapper.map(mockCompany, CompanyDTO.class);
	String expected = mapper.writeValueAsString(companyDTO);
	
	assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
	JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }
    
    @Test
    public void postShouldReturnValidCompany() throws Exception {
	when(companyService.create(any(CompanyCreateDTO.class), any(AuthUser.class)))
	.thenReturn(mockCompany);
    }

}

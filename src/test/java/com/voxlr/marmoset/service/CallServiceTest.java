package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuditableEntity;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.test.IntegrationTest;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

public class CallServiceTest extends IntegrationTest {
    
    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public CallService callService() {
            return new CallService();
        }
        
        @Bean
        public AuthorizationService authorizationService() {
            return new AuthorizationService();
        }
    }
    
    @Autowired
    private CallService callService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @MockBean
    private CallRepository callRepository;
    
    Call mockCall;
    
    @Before
    public void setup() throws Exception {
	mockCall = createAuditableEntity(Call.builder().companyId("123")
		.employeeNumber("+11234567890")
		.customerNumber("+11234567890")
		.recordingUrl("/recordings/123")
		.build());
	when(callRepository.findOne(mockCall.getId())).thenReturn(mockCall);
	when(callRepository.save(any(Call.class))).thenAnswer(i -> i.getArguments()[0]);
    }
    
    @Test
    public void getShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    Call call = callService.get("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void createShouldReturnCallForValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.TEAM_ADMIN,
		UserRole.MEMBER);
	
	CallCreateDTO callCreateDTO = CallCreateDTO.builder()
		.callSid(UUID.randomUUID().toString())
		.employeeNumber("+11234567890")
		.customerNumber("+11234567890")
		.strategy("Phrase 1")
		.strategy("Phrase 2")
		.build();
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCall.getId());
		Call call = callService.create(callCreateDTO, authUser);
		assertThat(call.getUserId(), is(authUser.getId()));
		assertThat(call.getCompanyId(), is(authUser.getCompanyId()));
	    });
	});
    }
    
    @Test
    public void createShouldBeInvalidForAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_READONLY);
	
	CallCreateDTO callCreateDTO = CallCreateDTO.builder()
		.callSid(UUID.randomUUID().toString())
		.employeeNumber("+11234567890")
		.customerNumber("+11234567890")
		.strategy("Phrase 1")
		.strategy("Phrase 2")
		.build();
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId("team123");
		Call call = callService.create(callCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
}

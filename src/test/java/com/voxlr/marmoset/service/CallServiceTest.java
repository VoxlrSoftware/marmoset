package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.model.CallOutcome.NONE;
import static com.voxlr.marmoset.model.CallOutcome.VOICEMAIL;
import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createCall;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.test.DataTest;

public class CallServiceTest extends DataTest {
    
    @Autowired
    private CallService callService;
    
    Company mockCompany;
    Team mockTeam;
    User mockUser;
    Call mockCall;
    
    @Override
    public void beforeTest() {
	mockCompany = createCompany("Test Company");
	mockTeam = createTeam(mockCompany.getId(), "Test Team");
	mockUser = createUser(mockCompany.getId(), mockTeam.getId());
	
	persistenceUtils.save(mockCompany, mockTeam, mockUser);
	mockCall = createCall(mockUser.getId());
    }
    
    @Test
    public void getShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    Call call = callService.get("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void getShouldReturnCallForValidAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockTeam.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		persistenceUtils.removeAllAndSave(Call.class, mockCall);
		Call call = callService.get(mockCall.getId(), authUser);
		assertThat(call, is(notNullValue()));
	    });
	});
    }
    
    @Test
    public void createShouldReturnCallForValidAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockTeam.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	CallCreateDTO callCreateDTO = CallCreateDTO.builder()
		.callSid(UUID.randomUUID().toString())
		.employeeNumber(new PhoneNumberHolder("+11234567890"))
		.customerNumber(new PhoneNumberHolder("+11234567890"))
		.strategy("Phrase 1")
		.strategy("Phrase 2")
		.build();
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		Call call = callService.create(callCreateDTO, authUser);
		assertThat(call.getUserId(), is(authUser.getId()));
		assertThat(call.getCompanyId(), is(authUser.getCompanyId()));
	    });
	});
    }
 
    @Test
    public void createShouldBeInvalidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId())
		);
	
	CallCreateDTO callCreateDTO = CallCreateDTO.builder()
		.callSid(UUID.randomUUID().toString())
		.employeeNumber(new PhoneNumberHolder("+11234567890"))
		.customerNumber(new PhoneNumberHolder("+11234567890"))
		.strategy("Phrase 1")
		.strategy("Phrase 2")
		.build();
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		Call call = callService.create(callCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void newCallShouldHaveInitializedValues() {
	CallCreateDTO callCreateDTO = CallCreateDTO.builder()
		.callSid(UUID.randomUUID().toString())
		.employeeNumber(new PhoneNumberHolder("+11234567890"))
		.customerNumber(new PhoneNumberHolder("+11234567890"))
		.strategy("Phrase 1")
		.strategy("Phrase 2")
		.build();
	AuthUser authUser = createAuthUser();
	Call call = callService.create(callCreateDTO, authUser);
	assertThat(call.getStatistics(), is(notNullValue()));
	assertThat(call.getCallOutcome(), is(NONE));
    }
    
    @Test
    public void updateShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	CallUpdateDTO callUpdateDTO = CallUpdateDTO.builder()
		.id("123")
		.callOutcome(VOICEMAIL)
		.build();
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    Call call = callService.update(callUpdateDTO, authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void updateShouldBeValidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockTeam.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	CallUpdateDTO callUpdateDTO = CallUpdateDTO.builder()
		.id(mockCall.getId())
		.callOutcome(VOICEMAIL)
		.build();
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		persistenceUtils.removeAllAndSave(Call.class, mockCall);
		Call call = callService.update(callUpdateDTO, authUser);
		assertThat(call, is(notNullValue()));
		assertThat(call.getCallOutcome(), is(VOICEMAIL));
	    });
	});
    }
    
    @Test
    public void updateShouldBeInvalidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId("123"),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
		);
	
	CallUpdateDTO callUpdateDTO = CallUpdateDTO.builder()
		.id(mockCall.getId())
		.callOutcome(VOICEMAIL)
		.build();
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		persistenceUtils.removeAllAndSave(Call.class, mockCall);
		Call call = callService.update(callUpdateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void deleteShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    callService.delete("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void deleteShouldBeValidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockTeam.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		persistenceUtils.removeAllAndSave(Call.class, mockCall);
		callService.delete(mockCall.getId(), authUser);
	    });
	});
    }
    
    @Test
    public void deleteShouldBeInvalidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId("123"),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
		);
	
	persistenceUtils.save(mockCall);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		callService.delete(mockCall.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getCallsByCompanyShouldAuthCompany() throws Exception {
	listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId())
	).stream().forEach(authUser -> {
	    wrapNoException(() -> {
    		callService.getCallsByCompanyId(
    			mockCompany.getId(),
    			authUser,
    			DateConstrained.builder()
    				.startDate(new DateTime())
    				.endDate(new DateTime())
    				.build(),
    			PageRequest.of(0, 20));
	    });
	});
	
	listOf(
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
	).stream().forEach(authUser -> {
	    wrapAssertException(() -> {
    		callService.getCallsByCompanyId(
    			mockCompany.getId(),
    			authUser,
    			DateConstrained.builder()
    				.startDate(new DateTime())
    				.endDate(new DateTime())
    				.build(),
    			PageRequest.of(0, 20));
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getCallsByUserShouldAuthCompany() throws Exception {
	listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
	).stream().forEach(authUser -> {
	    wrapNoException(() -> {
    		callService.getCallsByUserId(
    			mockUser.getId(),
    			authUser,
    			DateConstrained.builder()
    				.startDate(new DateTime())
    				.endDate(new DateTime())
    				.build(),
    			PageRequest.of(0, 20));
	    });
	});
	
	listOf(
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
	).stream().forEach(authUser -> {
	    wrapAssertException(() -> {
    		callService.getCallsByUserId(
    			mockUser.getId(),
    			authUser,
    			DateConstrained.builder()
    				.startDate(new DateTime())
    				.endDate(new DateTime())
    				.build(),
    			PageRequest.of(0, 20));
	    }, UnauthorizedUserException.class);
	});
    }
}

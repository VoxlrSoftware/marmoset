package com.voxlr.marmoset.service;


import static com.voxlr.marmoset.util.EntityTestUtils.createAuditableEntity;
import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.test.IntegrationTest;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

public class UserServiceTest extends IntegrationTest {

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public UserService userService() {
            return new UserService();
        }
        
        @Bean
        public AuthorizationService authorizationService() {
            return new AuthorizationService();
        }
        
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
        
        @Bean
        public ValidationService validationService() {
            return new ValidationService();
        }
    }
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private TeamService teamService;
    
    @MockBean
    private CompanyService companyService;
    
    User mockUser;
    UserCreateDTO userCreateDTO;
    
    @Before
    public void setup() {
	mockUser = createAuditableEntity(User.builder()
		    .firstName("Test")
		    .lastName("User")
		    .email("test.user@test.com")
		    .password(bCryptPasswordEncoder.encode("Password"))
		    .companyId("123")
		    .teamId("123")
		    .build());
	userCreateDTO = UserCreateDTO.builder()
		.firstName("TestA")
		.lastName("TestB")
		.password("Random")
		.companyId("company123")
		.teamId("team123")
		.email("testa.testb@gmail.com").build();
	when(teamService.validateExists(mockUser.getTeamId())).thenReturn(true);
	when(companyService.validateExists(mockUser.getCompanyId())).thenReturn(true);
	when(userRepository.findOne(mockUser.getId())).thenReturn(mockUser);
	when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    }
    
    @Test
    public void getShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    User user = userService.get("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void getShouldReturnUserForValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		mockUser.setRole(role);
		AuthUser authUser = AuthUser.buildFromUser(mockUser);
		
		User user = userService.get(mockUser.getId(), authUser);
		assertThat(user, is(mockUser));
	    });
	});
    }
    
    @Test
    public void getShouldFailForUsersWithDifferentCompanies() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		User user = userService.get(mockUser.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getShouldFailForUsersWithDifferentTeams() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		
		User user = userService.get(mockUser.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getShouldFailForNotOwnedUser() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser(UserRole.MEMBER);
	    User user = userService.get(mockUser.getId(), authUser);
	}, UnauthorizedUserException.class);
    }
    
    @Test
    public void createShouldReturnUserForSuperAdmin() throws Exception {
	AuthUser authUser = createAuthUser();
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	testCreateAndValidateResponse(authUser, roles);
    }
    
    @Test
    public void createShouldReturnUserForCompanyAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.COMPANY_ADMIN);
	authUser.setCompanyId("company123");
	
	List<UserRole> roles = listOf(
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	testCreateAndValidateResponse(authUser, roles);
    }
    
    @Test
    public void createHigherUserShouldFailForCompanyAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.COMPANY_ADMIN);
	authUser.setCompanyId("company123");
	
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN);
	
	testCreateAndFail(authUser, roles);
    }
    
    @Test
    public void createShouldReplaceCompanyAndTeamIdForCompanyAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN);
	authUser.setCompanyId("company456");
	authUser.setTeamId("team456");
	
	userCreateDTO.setCompanyId(null);
	userCreateDTO.setTeamId(null);
	
	User user = userService.create(userCreateDTO, authUser);
	assertThat(user.getCompanyId(), is(authUser.getCompanyId()));
	assertThat(user.getTeamId(), is(authUser.getTeamId()));
    }
    
    @Test
    public void createShouldReturnUserForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN);
	authUser.setCompanyId("company123");
	authUser.setTeamId("team123");
	
	List<UserRole> roles = listOf(
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	testCreateAndValidateResponse(authUser, roles);
    }
    
    @Test
    public void createHigherUserShouldFailForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN);
	authUser.setCompanyId("company123");
	authUser.setTeamId("team123");
	
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY);
	
	testCreateAndFail(authUser, roles);
    }
    
    @Test
    public void createShouldReplaceCompanyAndTeamIdForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN);
	authUser.setCompanyId("company456");
	authUser.setTeamId("team456");
	
	userCreateDTO.setCompanyId(null);
	userCreateDTO.setTeamId(null);
	
	User user = userService.create(userCreateDTO, authUser);
	assertThat(user.getCompanyId(), is(authUser.getCompanyId()));
	assertThat(user.getTeamId(), is(authUser.getTeamId()));
    }
    
    @Test
    public void createShouldFailForInvalidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		
		User user = userService.create(userCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void updateShouldBeSuccessfulForValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.TEAM_ADMIN,
		UserRole.MEMBER);
	
	UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
		.firstName("new first name")
		.lastName("New last name")
		.password("RandoPass")
		.build();
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockUser.getCompanyId());
		authUser.setTeamId(mockUser.getTeamId());
		authUser.setId(mockUser.getId());
		User user = userService.update(userUpdateDTO, authUser);
		assertThat(user.getFirstName(), is(userUpdateDTO.getFirstName()));
		assertThat(user.getLastName(), is(userUpdateDTO.getLastName()));
		assertThat(bCryptPasswordEncoder.matches(userUpdateDTO.getPassword(), user.getPassword()), is(true));
	    });
	});
    }
    
    @Test
    public void updateShouldNotBeValidForAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
		.firstName("new first name")
		.lastName("New last name")
		.password("RandoPass")
		.build();
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockUser.getCompanyId());
		authUser.setTeamId(mockUser.getTeamId());
		User user = userService.update(userUpdateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void deleteShouldBeValidForAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.TEAM_ADMIN,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockUser.getCompanyId());
		authUser.setTeamId(mockUser.getTeamId());
		authUser.setId(mockUser.getId());
		User user = userService.delete(mockUser.getId(), authUser);
		assertThat(user.isDeleted(), is(true));
	    });
	});
    }
    
    @Test
    public void deleteShouldBeInvalidForAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockUser.getCompanyId());
		authUser.setTeamId(mockUser.getTeamId());
		User user = userService.delete(mockUser.getId(), authUser);
		assertThat(user.isDeleted(), is(true));
	    }, UnauthorizedUserException.class);
	});
    }
    
    private void testCreateAndValidateResponse(AuthUser authUser, List<UserRole> createable) {
	createable.stream().forEach(role -> {
	    wrapNoException(() -> {
		userCreateDTO.setRole(role.getId());
		
		User user = userService.create(userCreateDTO, authUser);
		assertThat(user, is(notNullValue()));
		assertThat(user.getCompanyId(), is("company123"));
		assertThat(user.getTeamId(), is("team123"));
		assertThat(user.getRole(), is(role));
		assertThat(bCryptPasswordEncoder.matches("Random", user.getPassword()), is(true));
	    });
	});
    }
    
    private void testCreateAndFail(AuthUser authUser, List<UserRole> createable) {
	createable.stream().forEach(role -> {
	    wrapAssertException(() -> {
		userCreateDTO.setRole(role.getId());
		
		User user = userService.create(userCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
}

package com.voxlr.marmoset.service;


import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.service.domain.UserService;
import com.voxlr.marmoset.test.DataTest;

public class UserServiceTest extends DataTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    Company company;
    Team team;
    User mockUser;
    UserCreateDTO userCreateDTO;
    
    @Override
    public void beforeTest() {
	company = createCompany("Test");
	team = createTeam(company.getId(), "Test");
	mockUser = createUser(company.getId(), team.getId());
	mockUser.setEmail("test.user@test.com")
		.setPassword(bCryptPasswordEncoder.encode("Password"));
	
	persistenceUtils.save(company, team);
	
	userCreateDTO = UserCreateDTO.builder()
		.firstName("TestA")
		.lastName("TestB")
		.companyId(company.getId())
		.teamId(team.getId())
		.email("testa.testb@gmail.com").build();
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
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(company.getId()),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(team.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	persistenceUtils.save(mockUser);
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		User user = userService.get(mockUser.getId(), authUser);
		assertThat(user, is(notNullValue()));
	    });
	});
    }
    
    @Test
    public void getShouldFailForUsersWithDifferentCompanies() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId("123")
		);
	
	persistenceUtils.save(mockUser);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		User user = userService.get(mockUser.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getShouldFailForUsersWithDifferentTeams() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId("123"),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId("123")
		);
	
	persistenceUtils.save(mockUser);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		User user = userService.get(mockUser.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getShouldFailForNotOwnedUser() throws Exception {
	persistenceUtils.save(mockUser);
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser(UserRole.MEMBER);
	    User user = userService.get(mockUser.getId(), authUser);
	}, UnauthorizedUserException.class);
    }
    
    @Test
    public void getUsersByCompanyShouldReturnList() throws Exception {
//	persistenceUtils.save(mockUser);
//	AuthUser authUser = createAuthUser();
//	Page<User> users = userService.getUsersByCompany(company.getId(), authUser);
//	assertThat(users, is(notNullValue()));
//	assertThat(users.size(), is(1));
//	assertThat(users.get(0).getId(), is(mockUser.getId()));
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
	AuthUser authUser = createAuthUser(UserRole.COMPANY_ADMIN)
		.setCompanyId(company.getId());
	
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
	AuthUser authUser = createAuthUser(UserRole.COMPANY_ADMIN)
		.setCompanyId(company.getId());
	
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN);
	
	testCreateAndFail(authUser, roles);
    }
    
    @Test
    public void createShouldReplaceCompanyAndTeamIdForCompanyAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN)
		.setCompanyId(company.getId())
		.setTeamId(team.getId());
	
	userCreateDTO.setCompanyId(null);
	userCreateDTO.setTeamId(null);
	
	User user = userService.create(userCreateDTO, authUser);
	assertThat(user.getCompanyId(), is(authUser.getCompanyId()));
	assertThat(user.getTeamId(), is(authUser.getTeamId()));
    }
    
    @Test
    public void createShouldReturnUserForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN)
		.setCompanyId(company.getId())
		.setTeamId(team.getId());
	
	List<UserRole> roles = listOf(
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	testCreateAndValidateResponse(authUser, roles);
    }
    
    @Test
    public void createHigherUserShouldFailForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN)
		.setCompanyId(company.getId())
		.setTeamId(team.getId());
	
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY);
	
	testCreateAndFail(authUser, roles);
    }
    
    @Test
    public void createShouldReplaceCompanyAndTeamIdForTeamAdmin() throws Exception {
	AuthUser authUser = createAuthUser(UserRole.TEAM_ADMIN)
		.setCompanyId("company456")
		.setTeamId("team456");
	
	userCreateDTO.setCompanyId(null);
	userCreateDTO.setTeamId(null);
	
	User user = userService.create(userCreateDTO, authUser);
	assertThat(user.getCompanyId(), is(authUser.getCompanyId()));
	assertThat(user.getTeamId(), is(authUser.getTeamId()));
    }
    
    @Test
    public void createShouldFailForInvalidAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
		);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		User user = userService.create(userCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void updateShouldBeSuccessfulForValidAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
		.firstName("new first name")
		.lastName("New last name")
		.password("RandoPass")
		.id(mockUser.getId())
		.build();
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		persistenceUtils.removeAll(User.class);
		persistenceUtils.save(mockUser);
		User user = userService.update(userUpdateDTO, authUser);
		assertThat(user.getFirstName(), is(userUpdateDTO.getFirstName()));
		assertThat(user.getLastName(), is(userUpdateDTO.getLastName()));
		assertThat(bCryptPasswordEncoder.matches(userUpdateDTO.getPassword(), user.getPassword()), is(true));
	    });
	});
    }
    
    @Test
    public void updateShouldNotBeValidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
		);
	
	UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
		.firstName("new first name")
		.lastName("New last name")
		.password("RandoPass")
		.id(mockUser.getId())
		.build();
	
	persistenceUtils.save(mockUser);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		User user = userService.update(userUpdateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void deleteShouldBeValidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.SUPER_ADMIN),
		createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_ADMIN).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId(mockUser.getId())
		);
	
	authUsers.stream().forEach(authUser -> {
	    wrapNoException(() -> {
		persistenceUtils.removeAll(User.class);
		persistenceUtils.save(mockUser);
		User user = userService.delete(mockUser.getId(), authUser);
		assertThat(user.isInactive(), is(true));
	    });
	});
    }
    
    @Test
    public void deleteShouldBeInvalidForAccounts() throws Exception {
	List<AuthUser> authUsers = listOf(
		createAuthUser(UserRole.ADMIN),
		createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(company.getId()),
		createAuthUser(UserRole.TEAM_READONLY).setTeamId(team.getId()),
		createAuthUser(UserRole.MEMBER).setId("123")
		);

	persistenceUtils.save(mockUser);
	
	authUsers.stream().forEach(authUser -> {
	    wrapAssertException(() -> {
		authUser.setCompanyId(mockUser.getCompanyId());
		authUser.setTeamId(mockUser.getTeamId());
		User user = userService.delete(mockUser.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    private void testCreateAndValidateResponse(AuthUser authUser, List<UserRole> createable) {
	createable.stream().forEach(role -> {
	    persistenceUtils.removeAll(User.class);
	    persistenceUtils.save(mockUser);
	    wrapNoException(() -> {
		userCreateDTO.setRole(role.getId());
		
		User user = userService.create(userCreateDTO, authUser);
		assertThat(user, is(notNullValue()));
		assertThat(user.getCompanyId(), is(company.getId()));
		assertThat(user.getTeamId(), is(team.getId()));
		assertThat(user.getRole(), is(role));
	    });
	});
    }
    
    private void testCreateAndFail(AuthUser authUser, List<UserRole> createable) {
	createable.stream().forEach(role -> {
	    persistenceUtils.removeAll(User.class);
	    persistenceUtils.save(mockUser);
	    wrapAssertException(() -> {
		userCreateDTO.setRole(role.getId());
		
		User user = userService.create(userCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
}

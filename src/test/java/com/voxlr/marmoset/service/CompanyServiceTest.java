package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.model.persistence.dto.TeamCreateDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.test.IntegrationTest;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

public class CompanyServiceTest extends IntegrationTest {

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public CompanyService companyService() {
            return new CompanyService();
        }
        
        @Bean
        public AuthorizationService authorizationService() {
            return new AuthorizationService();
        }
    }
    
    @Autowired
    private CompanyService companyService;
    
    @MockBean
    private CompanyRepository companyRepository;
    
    @MockBean
    private TeamService teamService;
    
    Company mockCompany = createCompany("Test Company", "Random phrase");
    
    @Before
    public void setup() {
	when(companyRepository.findOne(mockCompany.getId())).thenReturn(mockCompany);
	when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArguments()[0]);
    }
    
    @Test
    public void getShouldThrowExceptionIfEntityDoesNotExist() {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    Company company = companyService.get("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void getShouldReturnCompanyForValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY);
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCompany.getId());
		Company company = companyService.get(mockCompany.getId(), authUser);
		assertThat(company, is(mockCompany));
	    });
	});
    }
    
    @Test
    public void getShouldFailForInvalidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCompany.getId());
		Company company = companyService.get(mockCompany.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void getShouldFailWhenCompanyIdIsDifferent() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		Company company = companyService.get(mockCompany.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void createShouldReturnNewCompanyForValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN);
	
	CompanyCreateDTO companyCreateDTO = CompanyCreateDTO.builder()
		.name("TestCompany").build();
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		Company company = companyService.create(companyCreateDTO, authUser);
		assertThat(company, is(notNullValue()));
	    });
	});
    }
    
    @Test
    public void createShouldFailForInvalidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	CompanyCreateDTO companyCreateDTO = CompanyCreateDTO.builder()
		.name("TestCompany").build();
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		Company company = companyService.create(companyCreateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void updateShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    CompanyUpdateDTO companyUpdateDTO = CompanyUpdateDTO.builder()
		    .id("123")
		    .name("Test").build();
	    Company company = companyService.update(companyUpdateDTO, authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void updateShouldBeSuccessfulWithValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.COMPANY_ADMIN);
	
	CompanyUpdateDTO companyUpdateDTO = modelMapper.map(mockCompany, CompanyUpdateDTO.class);
	companyUpdateDTO.setName("New name");
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCompany.getId());
		Company company = companyService.update(companyUpdateDTO, authUser);
		assertThat(company.getName(), is("New name"));
	    });
	});
    }
    
    @Test
    public void updateShouldFailWithInvalidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	CompanyUpdateDTO companyUpdateDTO = modelMapper.map(mockCompany, CompanyUpdateDTO.class);
	companyUpdateDTO.setName("New name");
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		Company company = companyService.update(companyUpdateDTO, authUser);
	    }, UnauthorizedUserException.class);
	});
    }
    
    @Test
    public void deleteShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
	wrapAssertException(() -> {
	    AuthUser authUser = createAuthUser();
	    companyService.delete("123", authUser);
	}, EntityNotFoundException.class);
    }
    
    @Test
    public void deleteShouldBeSuccessfulWithValidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.SUPER_ADMIN,
		UserRole.COMPANY_ADMIN);
	
	roles.stream().forEach(role -> {
	    wrapNoException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCompany.getId());
		companyService.delete(mockCompany.getId(), authUser);
	    });
	});
    }
    
    @Test
    public void deleteShouldFailWithInvalidAccounts() throws Exception {
	List<UserRole> roles = listOf(
		UserRole.ADMIN,
		UserRole.COMPANY_READONLY,
		UserRole.TEAM_ADMIN,
		UserRole.TEAM_READONLY,
		UserRole.MEMBER);
	
	roles.stream().forEach(role -> {
	    wrapAssertException(() -> {
		AuthUser authUser = createAuthUser(role);
		authUser.setCompanyId(mockCompany.getId());
		companyService.delete(mockCompany.getId(), authUser);
	    }, UnauthorizedUserException.class);
	});
    }

}

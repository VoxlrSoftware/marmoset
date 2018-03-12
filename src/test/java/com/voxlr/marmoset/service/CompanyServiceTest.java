package com.voxlr.marmoset.service;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CallStrategyDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.service.domain.CompanyService;
import com.voxlr.marmoset.test.DataTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import java.util.List;

import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CompanyServiceTest extends DataTest {

  @Autowired private CompanyService companyService;

  Company mockCompany;

  @Override
  public void beforeTest() {
    mockCompany =
        createCompany("Test Company", "Random phrase")
            .setPhoneNumber(new PhoneNumberHolder("+119099446352"))
            .setCallStrategies(
                listOf(
                    CallStrategy.createNew()
                        .update("Test Phrase", listOf("This is a test phrase"))));
  }

  @Test
  public void getShouldThrowExceptionIfEntityDoesNotExist() {
    wrapAssertException(
        () -> {
          AuthUser authUser = createAuthUser();
          Company company = companyService.get("123", authUser);
        },
        EntityNotFoundException.class);
  }

  @Test
  public void getShouldReturnCompanyForValidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.SUPER_ADMIN),
            createAuthUser(UserRole.ADMIN),
            createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
            createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()));

    persistenceUtils.save(mockCompany);

    authUsers
        .stream()
        .forEach(
            authUser -> {
              wrapNoException(
                  () -> {
                    authUser.setCompanyId(mockCompany.getId());
                    Company company = companyService.get(mockCompany.getId(), authUser);
                    assertThat(company, is(notNullValue()));
                    assertThat(company.getPhoneNumber().getNumber(), is("+119099446352"));
                  });
            });
  }

  @Test
  public void getShouldFailForInvalidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.TEAM_ADMIN).setCompanyId(mockCompany.getId()),
            createAuthUser(UserRole.TEAM_READONLY).setCompanyId(mockCompany.getId()),
            createAuthUser(UserRole.MEMBER).setCompanyId(mockCompany.getId()));

    persistenceUtils.save(mockCompany);

    authUsers
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    authUser.setCompanyId(mockCompany.getId());
                    Company company = companyService.get(mockCompany.getId(), authUser);
                  },
                  UnauthorizedUserException.class);
            });
  }

  @Test
  public void getShouldFailWhenCompanyIdIsDifferent() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"),
            createAuthUser(UserRole.COMPANY_READONLY).setCompanyId("123"));

    persistenceUtils.save(mockCompany);

    authUsers
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    Company company = companyService.get(mockCompany.getId(), authUser);
                  },
                  UnauthorizedUserException.class);
            });
  }

  @Test
  public void getCompanyStrategyShouldReturnMatchingStrategy() throws Exception {
    persistenceUtils.save(mockCompany);

    CallStrategy strategy = mockCompany.getCallStrategies().get(0);
    CallStrategy foundStrategy =
        companyService.findCallStrategy(mockCompany.getId(), strategy.getId());
    assertThat(foundStrategy, is(notNullValue()));
    assertThat(foundStrategy.getName(), is(strategy.getName()));
  }

  @Test
  public void createShouldReturnNewCompanyForValidAccounts() throws Exception {
    List<AuthUser> authUsers = listOf(createAuthUser(UserRole.SUPER_ADMIN));

    CompanyCreateDTO companyCreateDTO = CompanyCreateDTO.builder().name("TestCompany").build();

    authUsers
        .stream()
        .forEach(
            authUser -> {
              persistenceUtils.removeAll(Company.class);
              wrapNoException(
                  () -> {
                    Company company = companyService.create(companyCreateDTO, authUser);
                    assertThat(company, is(notNullValue()));
                  });
            });
  }

  @Test
  public void createShouldFailForInvalidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.ADMIN),
            createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"),
            createAuthUser(UserRole.COMPANY_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_ADMIN).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.MEMBER).setCompanyId("123"));

    CompanyCreateDTO companyCreateDTO = CompanyCreateDTO.builder().name("TestCompany").build();

    authUsers
        .stream()
        .forEach(
            authUser -> {
              persistenceUtils.removeAll(Company.class);
              wrapAssertException(
                  () -> {
                    Company company = companyService.create(companyCreateDTO, authUser);
                  },
                  UnauthorizedUserException.class);
            });
  }

  @Test
  public void updateShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
    wrapAssertException(
        () -> {
          AuthUser authUser = createAuthUser();
          CompanyUpdateDTO companyUpdateDTO =
              CompanyUpdateDTO.builder().id("123").name("Test").build();
          Company company = companyService.update(companyUpdateDTO, authUser);
        },
        EntityNotFoundException.class);
  }

  @Test
  public void updateShouldBeSuccessfulWithValidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.SUPER_ADMIN),
            createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()));

    CompanyUpdateDTO companyUpdateDTO = modelMapper.map(mockCompany, CompanyUpdateDTO.class);
    companyUpdateDTO.setName("New name");

    authUsers
        .stream()
        .forEach(
            authUser -> {
              persistenceUtils.removeAll(Company.class);
              persistenceUtils.save(mockCompany);
              wrapNoException(
                  () -> {
                    Company company = companyService.update(companyUpdateDTO, authUser);
                    assertThat(company.getName(), is("New name"));
                  });
            });
  }

  @Test
  public void updateShouldFailWithInvalidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.ADMIN),
            createAuthUser(UserRole.COMPANY_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_ADMIN).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.MEMBER).setCompanyId("123"));

    CompanyUpdateDTO companyUpdateDTO = modelMapper.map(mockCompany, CompanyUpdateDTO.class);
    companyUpdateDTO.setName("New name");

    persistenceUtils.save(mockCompany);

    authUsers
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    Company company = companyService.update(companyUpdateDTO, authUser);
                  },
                  UnauthorizedUserException.class);
            });
  }

  @Test
  public void updateShouldHandleCallStrategyUpdates() throws Exception {
    CompanyUpdateDTO companyUpdateDTO = CompanyUpdateDTO.builder().id(mockCompany.getId()).build();
    CallStrategyDTO callStrategyDTO =
        modelMapper.map(mockCompany.getCallStrategies().get(0), CallStrategyDTO.class);
    callStrategyDTO.setName("Updated Name");

    CallStrategyDTO callStrategyDTO2 =
        CallStrategyDTO.builder()
            .name("New Strategy")
            .phrases(listOf("This is a new phrase"))
            .build();

    companyUpdateDTO.setCallStrategies(listOf(callStrategyDTO, callStrategyDTO2));

    persistenceUtils.save(mockCompany);

    Company company = companyService.update(companyUpdateDTO, createAuthUser());
    assertThat(company.getCallStrategies().size(), is(2));

    CallStrategy firstStrategy = company.getCallStrategies().get(0);
    assertThat(firstStrategy.getId(), is(mockCompany.getCallStrategies().get(0).getId()));
    assertThat(firstStrategy.getName(), is("Updated Name"));

    CallStrategy secondStrategy = company.getCallStrategies().get(1);
    assertThat(secondStrategy.getId(), is(notNullValue()));
    assertThat(secondStrategy.getName(), is("New Strategy"));
    assertThat(secondStrategy.getPhrases().size(), is(1));
    assertThat(secondStrategy.getPhrases().get(0), is("This is a new phrase"));
  }

  @Test
  public void deleteShouldThrowExceptionIfEntityDoesNotExist() throws Exception {
    wrapAssertException(
        () -> {
          AuthUser authUser = createAuthUser();
          companyService.delete("123", authUser);
        },
        EntityNotFoundException.class);
  }

  @Test
  public void deleteShouldBeSuccessfulWithValidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.SUPER_ADMIN),
            createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId("123"));

    authUsers
        .stream()
        .forEach(
            authUser -> {
              persistenceUtils.removeAll(Company.class);
              persistenceUtils.save(mockCompany);
              wrapNoException(
                  () -> {
                    authUser.setCompanyId(mockCompany.getId());
                    companyService.delete(mockCompany.getId(), authUser);
                  });
            });
  }

  @Test
  public void deleteShouldFailWithInvalidAccounts() throws Exception {
    List<AuthUser> authUsers =
        listOf(
            createAuthUser(UserRole.ADMIN),
            createAuthUser(UserRole.COMPANY_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_ADMIN).setCompanyId("123"),
            createAuthUser(UserRole.TEAM_READONLY).setCompanyId("123"),
            createAuthUser(UserRole.MEMBER).setCompanyId("123"));

    persistenceUtils.save(mockCompany);

    authUsers
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    authUser.setCompanyId(mockCompany.getId());
                    companyService.delete(mockCompany.getId(), authUser);
                  },
                  UnauthorizedUserException.class);
            });
  }
}

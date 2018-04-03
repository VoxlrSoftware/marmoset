package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.EntityTestUtils.createAuthUser;
import static com.voxlr.marmoset.util.EntityTestUtils.createCall;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;
import static com.voxlr.marmoset.util.ListUtils.listOf;

import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.service.domain.CallAggregationService;
import com.voxlr.marmoset.test.DataTest;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

public class CallAggregationServiceTest extends DataTest {

  @Autowired
  private CallAggregationService callAggregationService;

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
  public void getCallsByCompanyShouldAuthCompany() throws Exception {
    listOf(
        createAuthUser(UserRole.SUPER_ADMIN),
        createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
        createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()))
        .stream()
        .forEach(
            authUser -> {
              wrapNoException(
                  () -> {
                    callAggregationService.getCallsByCompanyId(
                        mockCompany.getId(),
                        authUser,
                        DateConstrained.builder()
                            .startDate(new DateTime())
                            .endDate(new DateTime())
                            .build(),
                        CallField.getAll()
                            .stream()
                            .map(CallField::getName)
                            .collect(Collectors.toList()),
                        PageRequest.of(0, 20));
                  });
            });

    listOf(
        createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockCompany.getId()),
        createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
        createAuthUser(UserRole.MEMBER).setId(mockUser.getId()))
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    callAggregationService.getCallsByCompanyId(
                        mockCompany.getId(),
                        authUser,
                        DateConstrained.builder()
                            .startDate(new DateTime())
                            .endDate(new DateTime())
                            .build(),
                        CallField.getAll()
                            .stream()
                            .map(CallField::getName)
                            .collect(Collectors.toList()),
                        PageRequest.of(0, 20));
                  },
                  UnauthorizedUserException.class);
            });
  }

  @Test
  public void getCallsByUserShouldAuthCompany() throws Exception {
    listOf(
        createAuthUser(UserRole.SUPER_ADMIN),
        createAuthUser(UserRole.COMPANY_ADMIN).setCompanyId(mockCompany.getId()),
        createAuthUser(UserRole.COMPANY_READONLY).setCompanyId(mockCompany.getId()),
        createAuthUser(UserRole.MEMBER).setId(mockUser.getId()))
        .stream()
        .forEach(
            authUser -> {
              wrapNoException(
                  () -> {
                    callAggregationService.getCallsByUserId(
                        mockUser.getId(),
                        authUser,
                        DateConstrained.builder()
                            .startDate(new DateTime())
                            .endDate(new DateTime())
                            .build(),
                        CallField.getAll()
                            .stream()
                            .map(CallField::getName)
                            .collect(Collectors.toList()),
                        PageRequest.of(0, 20));
                  });
            });

    listOf(
        createAuthUser(UserRole.TEAM_ADMIN).setTeamId(mockCompany.getId()),
        createAuthUser(UserRole.TEAM_READONLY).setTeamId(mockCompany.getId()),
        createAuthUser(UserRole.MEMBER).setId("123"))
        .stream()
        .forEach(
            authUser -> {
              wrapAssertException(
                  () -> {
                    callAggregationService.getCallsByUserId(
                        mockUser.getId(),
                        authUser,
                        DateConstrained.builder()
                            .startDate(new DateTime())
                            .endDate(new DateTime())
                            .build(),
                        CallField.getAll()
                            .stream()
                            .map(CallField::getName)
                            .collect(Collectors.toList()),
                        PageRequest.of(0, 20));
                  },
                  UnauthorizedUserException.class);
            });
  }

}

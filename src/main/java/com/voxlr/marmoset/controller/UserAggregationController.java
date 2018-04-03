package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.controller.UserController.COMPANY_USER;

import com.voxlr.marmoset.aggregation.dto.UserAggregateDTO;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.dto.PageDTO;
import com.voxlr.marmoset.service.domain.UserAggregationService;
import com.voxlr.marmoset.util.MapperUtils;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserAggregationController extends ApiController {
  public static final String SUMMARY = "/summary";

  @Autowired private UserAggregationService userAggregationService;
  @Autowired private MapperUtils mapperUtils;

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_USER + SUMMARY)
  public ResponseEntity<?> getUsersByCompany(
      @PathVariable String companyId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      Pageable pageable,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    Page<UserAggregateDTO> results =
        userAggregationService.getUsersSummaryByCompany(
            companyId, authUser, dateConstrained, fields, page(pageable));
    PageDTO<UserAggregateDTO> mappedResults = mapperUtils.mapPage(results);
    return new ResponseEntity<>(mappedResults, HttpStatus.OK);
  }
}

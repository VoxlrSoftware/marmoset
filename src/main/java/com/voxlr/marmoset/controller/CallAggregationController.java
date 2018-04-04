package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.controller.CallController.COMPANY_CALL;
import static com.voxlr.marmoset.controller.CallController.USER_CALL;

import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.dto.PageDTO;
import com.voxlr.marmoset.service.domain.CallAggregationService;
import com.voxlr.marmoset.util.MapperUtils;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
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
public class CallAggregationController extends ApiController {
  @Autowired private CallAggregationService callAggregationService;
  @Autowired private MapperUtils mapperUtils;

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_CALL)
  public ResponseEntity<?> getCallsByCompany(
      @PathVariable ObjectId companyId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      Pageable pageable,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    Page<CallAggregateDTO> results =
        callAggregationService.getCallsByCompanyId(
            companyId, authUser, dateConstrained, fields, page(pageable));
    PageDTO<CallAggregateDTO> mappedResults = mapperUtils.mapPage(results);
    return new ResponseEntity<>(mappedResults, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_CALL + "/average")
  public ResponseEntity<?> averageCallsByCompany(
      @PathVariable ObjectId companyId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    RollupResultDTO result =
        callAggregationService.averageCallsByCompanyId(companyId, authUser, dateConstrained, fields);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_CALL + "/rollup")
  public ResponseEntity<?> rollupCallsByCompany(
      @PathVariable ObjectId companyId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      @RequestParam(defaultValue = "daily") String cadence,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    List<RollupResultDTO> result =
        callAggregationService.rollupCallsByCompanyId(companyId, authUser, dateConstrained, cadence, fields);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = USER_CALL)
  public ResponseEntity<?> getCallsByUser(
      @PathVariable ObjectId userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      Pageable pageable,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    Page<CallAggregateDTO> results =
        callAggregationService.getCallsByUserId(userId, authUser, dateConstrained, fields, page(pageable));
    PageDTO<CallAggregateDTO> mappedResults = mapperUtils.mapPage(results);
    return new ResponseEntity<>(mappedResults, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = USER_CALL + "/average")
  public ResponseEntity<?> averageCallsByUser(
      @PathVariable ObjectId userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    RollupResultDTO result =
        callAggregationService.averageCallsByUserId(userId, authUser, dateConstrained, fields);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = USER_CALL + "/rollup")
  public ResponseEntity<?> rollupCallsByUser(
      @PathVariable ObjectId userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @Valid @NotNull @RequestParam List<String> fields,
      @RequestParam(defaultValue = "daily") String cadence,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    List<RollupResultDTO> result =
        callAggregationService.rollupCallsByUserId(userId, authUser, dateConstrained, cadence, fields);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = USER_CALL + "/outcomes")
  public ResponseEntity<?> getCallOutcomesByUser(
      @PathVariable ObjectId userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    AggregateResultDTO result =
        callAggregationService.getCallOutcomesByUser(userId, authUser, dateConstrained);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_CALL + "/outcomes")
  public ResponseEntity<?> getCallOutcomesByCompany(
      @PathVariable ObjectId companyId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    DateConstrained dateConstrained =
        DateConstrained.builder().startDate(startDate).endDate(endDate).build();
    AggregateResultDTO result =
        callAggregationService.getCallOutcomesByCompany(companyId, authUser, dateConstrained);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}

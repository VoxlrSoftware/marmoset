package com.voxlr.marmoset.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.service.domain.CallService;

@RestController
@Validated
public class CallAggregationController {
    @Autowired
    private CallService callService;
    
    @RequestMapping(
	    method = RequestMethod.GET,
	    value = CallController.USER_CALL + "/callOutcomes")
    public ResponseEntity<?> averageCallsByUser(
	    @PathVariable String userId,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	AggregateResultDTO result = callService.getCallOutcomesByUser(userId, authUser, dateConstrained);
	return new ResponseEntity<AggregateResultDTO>(result, HttpStatus.OK);
    }
    
    @RequestMapping(
	    method = RequestMethod.GET,
	    value = CallController.COMPANY_CALL + "/callOutcomes")
    public ResponseEntity<?> averageCallsByCompany(
	    @PathVariable String companyId,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	AggregateResultDTO result = callService.getCallOutcomesByCompany(companyId, authUser, dateConstrained);
	return new ResponseEntity<AggregateResultDTO>(result, HttpStatus.OK);
    }
}

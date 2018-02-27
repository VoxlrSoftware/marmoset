package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.controller.CompanyController.COMPANY;
import static com.voxlr.marmoset.controller.UserController.USER;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallRequest;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.model.persistence.dto.PageDTO;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.util.MapperUtils;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
@Validated
public class CallController extends ApiController {
    public static final String CALL = "/call";
    public static final String CALL_REQUEST = CALL + "/request";
    public static final String COMPANY_CALL = COMPANY + "/{companyId}" + CALL;
    public static final String USER_CALL = USER + "/{userId}" + CALL;
    
    @Autowired
    private CallService callService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private MapperUtils mapperUtils;
    
    @RequestMapping(
	    method = RequestMethod.GET,
	    value = CALL + "/{id}")
    public ResponseEntity<?> get(
	    @PathVariable String id,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	Call call = callService.get(id, authUser);
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	    method = RequestMethod.POST,
	    value = CALL_REQUEST)
    public ResponseEntity<?> createRequest(
	    @Valid @RequestBody CallRequestCreateDTO callRequestCreateDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	CallRequest callRequest = callService.createRequest(callRequestCreateDTO, authUser);
	
	CallRequestDTO callRequestDTO = modelMapper.map(callRequest, CallRequestDTO.class);
	return new ResponseEntity<CallRequestDTO>(callRequestDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	    method = RequestMethod.POST,
	    value = CALL)
    public ResponseEntity<?> create(
	    @Valid @RequestBody CallCreateDTO callCreateDTO,
	    @AuthenticationPrincipal AuthUser authUser) {
	Call call = callService.create(callCreateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	    method = RequestMethod.PUT,
	    value = CALL)
    public ResponseEntity<?> update(
	    @Valid @RequestBody CallUpdateDTO callUpdateDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	Call call = callService.update(callUpdateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	    method = RequestMethod.PUT,
	    value = CALL + "/{id}")
    public ResponseEntity<?> update(
	    @PathVariable String id,
	    @Valid @RequestBody CallUpdateDTO callUpdateDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	callUpdateDTO.setId(id);
	Call call = callService.update(callUpdateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = COMPANY_CALL)
    public ResponseEntity<?> getCallsByCompany(
	    @PathVariable String companyId,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	    Pageable pageable,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	Page<CallAggregateDTO> results = callService.getCallsByCompanyId(companyId, authUser, dateConstrained, page(pageable));
	PageDTO<CallAggregateDTO> mappedResults = mapperUtils.mapPage(results);
	return new ResponseEntity<PageDTO<CallAggregateDTO>>(mappedResults, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = COMPANY_CALL + "/average")
    public ResponseEntity<?> averageCallsByCompany(
	@PathVariable String companyId,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	@Valid @NotNull @RequestParam List<CallAggregationField> fields,
	@AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	RollupResultDTO result = callService.averageCallsByCompanyId(companyId, authUser, dateConstrained, fields);
	return new ResponseEntity<RollupResultDTO>(result, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = COMPANY_CALL + "/rollup")
    public ResponseEntity<?> rollupCallsByCompany(
	@PathVariable String companyId,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	@Valid @NotNull @RequestParam List<CallAggregationField> fields,
	@RequestParam(defaultValue = "daily") RollupCadence cadence,
	@AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	List<RollupResultDTO> result = callService.rollupCallsByCompanyId(companyId, authUser, dateConstrained, cadence, fields);
	return new ResponseEntity<List<RollupResultDTO>>(result, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = USER_CALL)
    public ResponseEntity<?> getCallsByUser(
	    @PathVariable String userId,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	    Pageable pageable,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	Page<CallAggregateDTO> results = callService.getCallsByUserId(userId, authUser, dateConstrained, page(pageable));
	PageDTO<CallAggregateDTO> mappedResults = mapperUtils.mapPage(results);
	return new ResponseEntity<PageDTO<CallAggregateDTO>>(mappedResults, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = USER_CALL + "/average")
    public ResponseEntity<?> averageCallsByUser(
	@PathVariable String userId,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	@Valid @NotNull @RequestParam List<CallAggregationField> fields,
	@AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	RollupResultDTO result = callService.averageCallsByUserId(userId, authUser, dateConstrained, fields);
	return new ResponseEntity<RollupResultDTO>(result, HttpStatus.OK);
    }
    
    @RequestMapping(
	method = RequestMethod.GET,
	value = USER_CALL + "/rollup")
    public ResponseEntity<?> rollupCallsByUser(
	@PathVariable String userId,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime startDate,
	@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime endDate,
	@Valid @NotNull @RequestParam List<CallAggregationField> fields,
	@RequestParam(defaultValue = "daily") RollupCadence cadence,
	@AuthenticationPrincipal AuthUser authUser) throws Exception {
	DateConstrained dateConstrained = DateConstrained.builder()
		.startDate(startDate)
		.endDate(endDate).build();
	List<RollupResultDTO> result = callService.rollupCallsByUserId(userId, authUser, dateConstrained, cadence, fields);
	return new ResponseEntity<List<RollupResultDTO>>(result, HttpStatus.OK);
    }
}

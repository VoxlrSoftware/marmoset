package com.voxlr.marmoset.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.service.CallService;

@RestController
@RequestMapping("/api/call")
public class CallController {
    @Autowired
    private CallService callService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(method = RequestMethod.GET, value = "{id}")
    public ResponseEntity<?> get(
	    @PathVariable String id,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	Call call = callService.get(id, authUser);
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(
	    @Valid @RequestBody CallCreateDTO callCreateDTO,
	    @AuthenticationPrincipal AuthUser authUser) {
	Call call = callService.create(callCreateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "{id}")
    public ResponseEntity<?> update(
	    @PathVariable String id,
	    @Valid @RequestBody CallUpdateDTO callUpdateDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	callUpdateDTO.setId(id);
	Call call = callService.update(callUpdateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
}

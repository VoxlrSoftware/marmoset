package com.voxlr.marmoset.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallDTO;
import com.voxlr.marmoset.service.CallService;

@RestController
@RequestMapping("/api/call")
public class CallController {
    @Autowired
    private CallService callService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?> createCall(
	    @RequestBody CallCreateDTO callCreateDTO,
	    @AuthenticationPrincipal AuthUser authUser) {
	Call call = callService.create(callCreateDTO, authUser);
	
	CallDTO callDTO = modelMapper.map(call, CallDTO.class);
	return new ResponseEntity<CallDTO>(callDTO, HttpStatus.OK);
    }
    
}

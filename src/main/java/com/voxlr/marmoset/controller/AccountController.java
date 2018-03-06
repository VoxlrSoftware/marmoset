package com.voxlr.marmoset.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import com.voxlr.marmoset.service.TwilioService;
import com.voxlr.marmoset.service.domain.UserService;
import com.voxlr.marmoset.service.domain.ValidationRequestService;

@RestController
public class AccountController extends ApiController {
    public static final String ACCOUNT = "/account";
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ValidationRequestService validationRequestService;
    
    @Autowired
    private TwilioService twilioService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(
	    method=RequestMethod.GET,
	    value=ACCOUNT)
    public ResponseEntity<?> get(@AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	User user = userService.get(authUser);
	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
}

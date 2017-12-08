package com.voxlr.marmoset.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.service.UserService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserService userService;
    
    @Autowired
    AuthorizationService authorizationService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(method=RequestMethod.GET, value="{id}")
    public ResponseEntity<?> getUser(@PathVariable String id, @AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	User user = userRepository.findOne(id);
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO, @AuthenticationPrincipal AuthUser authUser) throws MethodArgumentNotValidException {
	if (!authorizationService.canCreate(authUser, User.class)) {
	    throw new UnauthorizedUserException("Account unauthorized to create user");
	}

	User user = userService.createUser(userCreateDTO, authUser);

	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
}

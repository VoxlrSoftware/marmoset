package com.voxlr.marmoset.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.RemovedEntityDTO;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.service.UserService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(method=RequestMethod.GET, value="{id}")
    public ResponseEntity<?> get(@PathVariable String id, @AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	User user = userService.get(id, authUser);
	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO, @AuthenticationPrincipal AuthUser authUser) throws MethodArgumentNotValidException {
	User user = userService.create(userCreateDTO, authUser);

	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="{id}")
    public ResponseEntity<?> update(@PathVariable String id,
	    @Valid @RequestBody UserUpdateDTO userUpdateDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	userUpdateDTO.setId(id);
	User user = userService.update(userUpdateDTO, authUser);
	
	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="{id}")
    public ResponseEntity<?> delete(@PathVariable String id,
	    @AuthenticationPrincipal AuthUser authUser) throws EntityNotFoundException {
	userService.delete(id, authUser);
	
	RemovedEntityDTO removedEntityDTO = new RemovedEntityDTO(id);
	
	return new ResponseEntity<RemovedEntityDTO>(removedEntityDTO, HttpStatus.OK);
    }
}

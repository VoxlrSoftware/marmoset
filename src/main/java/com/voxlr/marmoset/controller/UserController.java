package com.voxlr.marmoset.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.User;
import com.voxlr.marmoset.model.dto.UserCreateDTO;
import com.voxlr.marmoset.model.dto.UserDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @RequestMapping(method=RequestMethod.GET, value="{id}")
    public ResponseEntity<?> getUser(@PathVariable String id, Principal principal) throws EntityNotFoundException {
	User user = userRepository.findOne(id);
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?>  createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) throws MethodArgumentNotValidException {
	User user = modelMapper.map(userCreateDTO, User.class);
	user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	userRepository.save(user);

	UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }
    
}
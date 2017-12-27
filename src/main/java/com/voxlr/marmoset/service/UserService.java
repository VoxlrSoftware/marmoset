package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    AuthorizationService authorizationService;
    
    @Autowired
    ValidationService validationService;
    
    public boolean validateUniqueEmail(String email) {
	return userRepository.findEmailByEmail(email) == null;
    }
    
    public User get(String id, AuthUser authUser) throws EntityNotFoundException {
	User user = userRepository.findOne(id);
	
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	return user;
    }
    
    public User create(UserCreateDTO userCreateDTO, AuthUser authUser) {
	if (!authorizationService.canCreate(authUser, User.class)) {
	    throw new UnauthorizedUserException("Account unauthorized to create user");
	}
	
	validationService.validate(authUser, userCreateDTO);
	
	User user = modelMapper.map(userCreateDTO, User.class);
	user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	user = userRepository.save(user);
	
	return user;
    }
    
    public User update(UserUpdateDTO userUpdateDTO, AuthUser authUser) throws EntityNotFoundException {
	User user = userRepository.findOne(userUpdateDTO.getId());
	
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", userUpdateDTO.getId());
	}
	
	if (!authorizationService.canWrite(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	validationService.validate(authUser, userUpdateDTO);
	
	if (userUpdateDTO.getFirstName() != null) {
	    user.setFirstName(userUpdateDTO.getFirstName());
	}
	
	if (userUpdateDTO.getLastName() != null) {
	    user.setLastName(userUpdateDTO.getLastName());
	}
	
	if (userUpdateDTO.getPassword() != null) {
	    user.setPassword(bCryptPasswordEncoder.encode(userUpdateDTO.getPassword()));
	}
	
	if (userUpdateDTO.getRole() != null) {
	    user.setRoleString(userUpdateDTO.getRole());
	}
	
	if (userUpdateDTO.getTeamId() != null) {
	    // set team stuff
	}
	
	return user;
    }
    
    public User delete(String id, AuthUser authUser) throws EntityNotFoundException {
	User user = userRepository.findOne(id);
	
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	if (!authorizationService.canWrite(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete company");
	}
	
	user.setDeleted(true);
	return userRepository.save(user);
    }
}

package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
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
	
	updateCreateDTOWithFields(userCreateDTO, authUser);

	User user = modelMapper.map(userCreateDTO, User.class);
	user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	userRepository.save(user);
	
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
	
	if (userUpdateDTO.getFirstName() != null) {
	    user.setFirstName(userUpdateDTO.getFirstName());
	}
	
	if (userUpdateDTO.getLastName() != null) {
	    user.setLastName(userUpdateDTO.getLastName());
	}
	
	if (userUpdateDTO.getPassword() != null) {
	    user.setPassword(userUpdateDTO.getPassword());
	}
	
	if (userUpdateDTO.getRole() != null) {
	    user.setRoleString(userUpdateDTO.getRole());
	}
	
	if (userUpdateDTO.getTeamId() != null) {
	    // set team stuff
	}
	
	return user;
    }
    
    void updateCreateDTOWithFields(UserCreateDTO userCreateDTO, AuthUser authUser) {
	if (!authUser.hasAuthority(Authority.MODIFY_ALL) || userCreateDTO.getCompanyId() == null) {
	    userCreateDTO.setCompanyId(authUser.getCompanyId());
	}

	if (!authUser.hasCapability(Authority.MODIFY_COMPANY) || userCreateDTO.getTeamId() == null) {
	    userCreateDTO.setTeamId(authUser.getTeamId());
	}
	
	if (userCreateDTO.getRole() != null) {
	    UserRole desiredRole = UserRole.get(userCreateDTO.getRole());
	    if (!authUser.isRoleAbove(desiredRole)) {
		throw new UnauthorizedUserException("Account unauthorized to create user with role [" + desiredRole.getId() + "].");
	    }
	}
    }
    
    public void delete(String id, AuthUser authUser) throws EntityNotFoundException {
	User user = userRepository.findOne(id);
	
	if (user == null) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	if (!authorizationService.canWrite(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete company");
	}
	
	userRepository.delete(user);
    }
}

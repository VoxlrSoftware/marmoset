package com.voxlr.marmoset.service.domain;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.service.ValidateableService;

@Service
public class UserService extends ValidateableService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    AuthorizationService authorizationService;
    
    @Autowired
    CompanyService companyService;
    
    public boolean validateUniqueEmail(String email) {
	return userRepository.findEmailByEmail(email) == null;
    }
    
    public User getInternal(String id) throws EntityNotFoundException {
	Optional<User> user = userRepository.findById(id);
	
	if (!user.isPresent()) {
	    throw new EntityNotFoundException(User.class, "id", id);
	}
	
	return user.get();
    }
    
    public User get(AuthUser authUser) throws EntityNotFoundException {
	return get(authUser.getId(), authUser);
    }
    
    public User get(String id, AuthUser authUser) throws EntityNotFoundException {
	User user = getInternal(id);
	
	if (!authorizationService.canRead(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	return user;
    }
    
    public Page<User> getUsersByCompany(String companyId, Pageable pageable, AuthUser authUser) throws EntityNotFoundException {
	Company company = companyService.get(companyId, authUser);
	
	Page<User> users = userRepository.findAllByCompany(company.getId(), pageable);
	return users;
    }
    
    public User create(UserCreateDTO userCreateDTO, AuthUser authUser) throws Exception {
	if (!authorizationService.canCreate(authUser, User.class)) {
	    throw new UnauthorizedUserException("Account unauthorized to create user");
	}
	
	validate(authUser, userCreateDTO);
	
	User user = modelMapper.map(userCreateDTO, User.class);
	user.setPassword(passwordEncoder.encode("Password"));
	user = userRepository.save(user);
	
	return user;
    }
    
    public User update(UserUpdateDTO userUpdateDTO, AuthUser authUser) throws Exception {
	User user = getInternal(userUpdateDTO.getId());
	
	if (!authorizationService.canWrite(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	validate(authUser, userUpdateDTO);
	
	if (userUpdateDTO.getFirstName() != null) {
	    user.setFirstName(userUpdateDTO.getFirstName());
	}
	
	if (userUpdateDTO.getLastName() != null) {
	    user.setLastName(userUpdateDTO.getLastName());
	}
	
	if (userUpdateDTO.getPassword() != null) {
	    user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
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
	User user = getInternal(id);
	
	if (!authorizationService.canWrite(authUser, user)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete user");
	}
	
	user.setInactive(true);
	return userRepository.save(user);
    }
}

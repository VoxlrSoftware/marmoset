package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public boolean validateUniqueEmail(String email) {
	return userRepository.findEmailByEmail(email) == null;
    }
    
    public User createUser(UserCreateDTO userCreateDTO, AuthUser authUser) {
	updateCreateDTOWithFields(userCreateDTO, authUser);

	User user = modelMapper.map(userCreateDTO, User.class);
	user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	userRepository.save(user);
	
	return user;
    }
    
    void updateCreateDTOWithFields(UserCreateDTO userCreateDTO, AuthUser authUser) {
	if (!authUser.hasAuthority(Authority.MODIFY_ALL) || userCreateDTO.getCompanyId() == null) {
	    userCreateDTO.setCompanyId(authUser.getCompanyId());
	}

	if (!authUser.hasAuthority(Authority.MODIFY_COMPANY) || userCreateDTO.getTeamId() == null) {
	    userCreateDTO.setTeamId(authUser.getTeamId());
	}
    }
}

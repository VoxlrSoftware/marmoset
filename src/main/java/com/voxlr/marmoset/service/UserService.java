package com.voxlr.marmoset.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    
    public boolean validateUsername(String username) {
	return userRepository.findUsernameByUsername(username) != null;
    }
}

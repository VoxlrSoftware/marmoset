package com.voxlr.marmoset.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    
    public boolean validateUniqueEmail(String email) {
	return userRepository.findEmailByEmail(email) == null;
    }
}

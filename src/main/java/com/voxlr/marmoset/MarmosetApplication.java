package com.voxlr.marmoset;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.voxlr.marmoset.model.map.UserCreateMapper;

@SpringBootApplication
public class MarmosetApplication {
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ModelMapper modelMapper() {
	ModelMapper modelMapper = new ModelMapper();
	modelMapper.addMappings(new UserCreateMapper());
	return modelMapper;
    }

    public static void main(String[] args) {
	SpringApplication.run(MarmosetApplication.class, args);
    }
}

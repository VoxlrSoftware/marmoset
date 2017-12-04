package com.voxlr.marmoset.model.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.voxlr.marmoset.model.User;

public class UserCreateDTOTest {
    private static final ModelMapper modelMapper = new ModelMapper();
    
    @Test
    public void checkUserMapping() {
	UserCreateDTO createDTO = new UserCreateDTO();
	createDTO.setCompanyId("123");
	createDTO.setFirstName("Michael");
	createDTO.setLastName("Gagliardo");
	createDTO.setUsername("mgagliardo");
	createDTO.setPassword("Password");
	
	User user = modelMapper.map(createDTO, User.class);
	assertEquals(user.getCompanyId(), createDTO.getCompanyId());
	assertEquals(user.getFirstName(), createDTO.getFirstName());
	assertEquals(user.getLastName(), createDTO.getLastName());
	assertEquals(user.getUsername(), createDTO.getUsername());
	assertEquals(user.getPassword(), createDTO.getPassword());
    }

}

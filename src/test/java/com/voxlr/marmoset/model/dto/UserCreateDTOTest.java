package com.voxlr.marmoset.model.dto;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;

public class UserCreateDTOTest {
    
    private ModelMapper modelMapper;
    
    @Test
    public void checkUserMapping() {
	UserCreateDTO createDTO = new UserCreateDTO();
	createDTO.setCompanyId("123");
	createDTO.setFirstName("Michael");
	createDTO.setLastName("Gagliardo");
	createDTO.setEmail("mgagliardo@a.com");
	createDTO.setPassword("Password");
	
//	User user = modelMapper.map(createDTO, User.class);
//	assertEquals(user.getCompanyId(), createDTO.getCompanyId());
//	assertEquals(user.getFirstName(), createDTO.getFirstName());
//	assertEquals(user.getLastName(), createDTO.getLastName());
//	assertEquals(user.getEmail(), createDTO.getEmail());
//	assertEquals(user.getPassword(), createDTO.getPassword());
    }

}

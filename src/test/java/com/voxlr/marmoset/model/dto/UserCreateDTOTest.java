package com.voxlr.marmoset.model.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.voxlr.marmoset.model.persistence.User;
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

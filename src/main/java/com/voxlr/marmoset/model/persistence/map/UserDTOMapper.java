package com.voxlr.marmoset.model.persistence.map;

import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;

@Component
public class UserDTOMapper extends TypeMapConfigurer<User, UserDTO> {

    @Override
    public void configure(TypeMap<User, UserDTO> typeMap) {
	
	typeMap.addMappings(Mapping -> {
	    Mapping.map(User::getCompanyId, UserDTO::setCompanyId);
	    Mapping.map(User::getTeamId, UserDTO::setTeamId);
	    Mapping.map(User::getId, UserDTO::setId);
	});
    }
 
}

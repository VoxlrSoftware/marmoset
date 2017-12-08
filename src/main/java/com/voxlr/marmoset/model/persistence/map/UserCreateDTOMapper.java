package com.voxlr.marmoset.model.persistence.map;

import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;

@Component
public class UserCreateDTOMapper extends TypeMapConfigurer<UserCreateDTO, User> {

    @Override
    public void configure(TypeMap<UserCreateDTO, User> typeMap) {

	typeMap.addMappings(mapper -> {
	    mapper.map(UserCreateDTO::getCompanyId, User::setCompanyId);
	    mapper.map(UserCreateDTO::getTeamId, User::setTeamId);
	    mapper.skip(User::setId);
	});
    }
 
}

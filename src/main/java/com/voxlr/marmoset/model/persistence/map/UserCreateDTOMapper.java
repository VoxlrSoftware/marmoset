package com.voxlr.marmoset.model.persistence.map;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class UserCreateDTOMapper extends TypeMapConfigurer<UserCreateDTO, User> {

  @Override
  public void configure(TypeMap<UserCreateDTO, User> typeMap) {

    typeMap.addMappings(
        mapper -> {
          mapper.skip(User::setId);
          mapper.map(UserCreateDTO::getCompanyId, User::setCompanyId);
          mapper.map(UserCreateDTO::getTeamId, User::setTeamId);
          mapper.map(UserCreateDTO::getRole, User::setRoleString);
        });
  }
}

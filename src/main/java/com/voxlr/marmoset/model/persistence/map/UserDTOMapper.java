package com.voxlr.marmoset.model.persistence.map;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper extends TypeMapConfigurer<User, UserDTO> {

  @Override
  public void configure(TypeMap<User, UserDTO> typeMap) {

    typeMap.addMappings(
        Mapping -> {
          Mapping.map(User::getCompanyId, UserDTO::setCompanyId);
          Mapping.map(User::getTeamId, UserDTO::setTeamId);
          Mapping.map(User::getId, UserDTO::setId);
          Mapping.map(User::getFullName, UserDTO::setFullName);
          Mapping.map(
              src -> src.getRole() != null ? src.getRole().getId() : null, UserDTO::setRole);
        });
  }
}

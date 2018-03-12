package com.voxlr.marmoset.model.persistence.map;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import org.modelmapper.TypeMap;

public class CallCreateDTOMapper extends TypeMapConfigurer<CallCreateDTO, Call> {

  @Override
  public void configure(TypeMap<CallCreateDTO, Call> typeMap) {}
}

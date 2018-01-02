package com.voxlr.marmoset.model.persistence.map;

import org.modelmapper.TypeMap;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;

public class CallCreateDTOMapper extends TypeMapConfigurer<CallCreateDTO, Call>{

    @Override
    public void configure(TypeMap<CallCreateDTO, Call> typeMap) {

    }

}

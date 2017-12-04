package com.voxlr.marmoset.model.map;

import org.modelmapper.PropertyMap;

import com.voxlr.marmoset.model.User;
import com.voxlr.marmoset.model.dto.UserCreateDTO;

public class UserCreateMapper extends PropertyMap<UserCreateDTO, User> {

    @Override
    protected void configure() {
	skip().setId(null); // ModelMapper would map companyId to id
    }
 
}

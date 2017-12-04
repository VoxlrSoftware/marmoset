package com.voxlr.marmoset.model.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.voxlr.marmoset.validation.UsernameConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
    
    @NotBlank
    private String companyId;
    
    @NotNull
    private String firstName;
    
    @NotNull
    private String lastName;
    
    @NotBlank
    private String password;

    @NotBlank
    @UsernameConstraint
    private String username;
}

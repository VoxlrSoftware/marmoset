package com.voxlr.marmoset.model.persistence.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.voxlr.marmoset.validation.UniqueEmailConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    @NotBlank
    private String companyId;

    @NotBlank
    private String teamId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    @UniqueEmailConstraint
    private String email;
    
    private String role;
}

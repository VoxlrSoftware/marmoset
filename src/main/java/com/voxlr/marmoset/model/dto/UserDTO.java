package com.voxlr.marmoset.model.dto;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotBlank
    private String companyId;

    @NotBlank
    private String teamId;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;

    @NotBlank
    private String email;
    
    @NotBlank
    private String id;
}

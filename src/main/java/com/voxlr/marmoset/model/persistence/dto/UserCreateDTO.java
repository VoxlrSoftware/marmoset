package com.voxlr.marmoset.model.persistence.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.voxlr.marmoset.validation.constraint.CompanyExistsConstraint;
import com.voxlr.marmoset.validation.constraint.TeamExistsConstraint;
import com.voxlr.marmoset.validation.constraint.UniqueEmailConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDTO {

    @CompanyExistsConstraint
    private String companyId;

    @TeamExistsConstraint
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

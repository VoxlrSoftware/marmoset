package com.voxlr.marmoset.model.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.voxlr.marmoset.validation.UsernameConstraint;

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
    
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

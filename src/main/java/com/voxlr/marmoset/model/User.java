package com.voxlr.marmoset.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@EnableMongoAuditing
public class User extends AuditModel {
    @NotBlank
    private String companyId;
    
    @NotNull
    private String firstName;
    
    @NotNull
    private String lastName;
    
    @NotBlank
    private String password;
    
    @NotBlank
    @Indexed(unique = true)
    private String username;
    
    @DBRef
    public List<Role> roles;
    
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}

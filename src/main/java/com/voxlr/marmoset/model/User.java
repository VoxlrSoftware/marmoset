package com.voxlr.marmoset.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
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
}

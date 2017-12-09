package com.voxlr.marmoset.model.persistence;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.TeamScopedEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "companyId_teamId", def = "{'companyId' : 1, 'teamId': 1}")
})
public class User extends AuditModel implements TeamScopedEntity {
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
    @Indexed(unique = true)
    private String email;
    
    private UserRole role = UserRole.MEMBER;
    
    public void setRoleString(String role) {
	UserRole userRole = UserRole.get(role);
	if (userRole != null) {
	    this.setRole(userRole);
	}
    }
}

package com.voxlr.marmoset.model.persistence;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.Phoneable;
import com.voxlr.marmoset.model.TeamScopedEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Document(collection = "users")
@EnableMongoAuditing
@Getter
@Setter
@Builder
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "id_companyId_teamId", def = "{'_id': 1, 'companyId': 1, 'teamId': 1}"),
    @CompoundIndex(name = "active_email", def = "{'inactive': 1, 'email': 1}"),
    @CompoundIndex(name = "active_companyId_teamId", def = "{'inactive': 1, 'companyId' : 1, 'teamId': 1}")
})
@Accessors(chain = true)
public class User extends AuditModel implements TeamScopedEntity, Phoneable<User> {
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
    
    private PhoneNumberHolder phoneNumber;
    
    @Field("inactive")
    @Builder.Default
    private boolean isInactive = false;
    
    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;
    
    @Builder.Default
    private UserRole role = UserRole.MEMBER;
    
    public User() {
	this.role = UserRole.MEMBER;
    }
    
    public void setRoleString(String role) {
	UserRole userRole = UserRole.get(role);
	if (userRole != null) {
	    this.setRole(userRole);
	}
    }
    
    public String getFullName() {
	return firstName + " " + lastName;
    }
}

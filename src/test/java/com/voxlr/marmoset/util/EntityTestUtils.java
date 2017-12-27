package com.voxlr.marmoset.util;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.AuditModel;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Company.CompanyBuilder;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.User.UserBuilder;

public class EntityTestUtils {
    public static String generateId() {
	return UUID.randomUUID().toString();
    }
    
    public static <T extends AuditModel> T createAuditableEntity(T entity) {
	entity.setId(generateId());
	entity.setCreateDate(new Date());
	entity.setLastModified(new Date());
	return entity;
    }
    
    public static CallStrategy createCallStrategy(String phrase) {
	return CallStrategy.builder()
		.phrase(phrase)
		.createDate(new Date())
		.modifiedDate(new Date())
		.build();
    }
    
    public static Company createCompany(String name, String... phrases) {
	CompanyBuilder builder = Company.builder()
		.name(name);
	
	Arrays.stream(phrases).forEach(phrase -> {
	    builder.callStrategy(createCallStrategy(phrase));
	});
	
	return createAuditableEntity(builder.build());
    }
    
    public static User createUser(String firstName) {
	UserBuilder builder = User.builder()
		.firstName(firstName);
	
	return createAuditableEntity(builder.build());
    }
    
    public static AuthUser createAuthUser() {
	return createAuthUser(UserRole.SUPER_ADMIN);
    }
    
    public static AuthUser createAuthUser(UserRole role) {
	User user = createAuditableEntity(User.builder()
		.email("test@test.com")
		.password("Password")
		.role(role)
		.build());
	return AuthUser.buildFromUser(user);
    }
}

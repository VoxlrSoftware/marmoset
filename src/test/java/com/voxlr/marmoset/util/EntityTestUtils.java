package com.voxlr.marmoset.util;

import static com.voxlr.marmoset.util.ListUtils.listOf;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.joda.time.DateTime;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.AuditModel;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Company.CompanyBuilder;
import com.voxlr.marmoset.model.persistence.Entity;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.Team.TeamBuilder;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.User.UserBuilder;

public class EntityTestUtils {
    public static String generateId() {
	return UUID.randomUUID().toString();
    }
    
    public static <T extends Entity> T createEntity(T entity) {
	entity.setId(generateId());
	return entity;
    }
    
    public static <T extends AuditModel> T createAuditableEntity(T entity) {
	entity.setId(generateId());
	entity.setCreateDate(new DateTime());
	entity.setLastModified(new DateTime());
	return entity;
    }
    
    public static CallStrategy createCallStrategy(String name, List<String> phrases) {
	CallStrategy callStrategy = CallStrategy.builder()
		.phrases(phrases)
		.build();
	callStrategy.setCreateDate(new DateTime());
	callStrategy.setLastModified(new DateTime());
	return callStrategy;
    }
    
    public static Company createCompany(String name, String... phrases) {
	CompanyBuilder builder = Company.builder()
		.name(name);
	
	IntStream.range(0, phrases.length)
		.forEach(idx -> {
		    builder.callStrategy(createCallStrategy("Phrase " + idx, listOf(phrases[idx])));
		});
	
	return createAuditableEntity(builder.build());
    }
    
    public static Team createTeam(String companyId, String name) {
	TeamBuilder builder = Team.builder()
		.companyId(companyId)
		.name(name);
	
	return createAuditableEntity(builder.build());
	
    }
    
    public static User createUser(String companyId, String teamId) {
	UserBuilder builder = User.builder()
		.companyId(companyId)
		.teamId(teamId);
	
	return createAuditableEntity(builder.build());
    }
    
    public static Call createCall(String userId) {
	return createAuditableEntity(Call.builder().userId(userId).build());
    }
    
    public static Call createCall(Call.CallBuilder callBuilder) {
	return createAuditableEntity(callBuilder.build());
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

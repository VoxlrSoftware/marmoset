package com.voxlr.marmoset.changelogs;

import java.util.Date;

import org.springframework.data.mongodb.core.MongoTemplate;import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;
import com.voxlr.marmoset.auth.UserRole;

@ChangeLog(order = "0001")
public class DBM_001_InitializeVoxlr {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private String companyId;
    private String teamId;
    
    @ChangeSet(order = "0001", id = "createAdminCompany", author = "mgagliardo", runAlways = true)
    public void createAdminCompany(MongoTemplate mongoTemplate) throws Exception {
	DBCollection collection = mongoTemplate.getCollection("companies");
	DBObject query = QueryBuilder.start().put("name").is("VoxlrAdmin").get();
	DBObject update = BasicDBObjectBuilder.start()
				.add("name", "VoxlrAdmin").get();
	WriteResult result = collection.update(query, update, true, false);
	if (result.getN() != 1) {
	    throw new Exception("Unable to create admin company");
	}
	
	companyId = collection.findOne(query).get("_id").toString();
    }
    
    @ChangeSet(order = "0002", id = "createAdminTeam", author = "mgagliardo", runAlways = true)
    public void createAdminTeam(MongoTemplate mongoTemplate) throws Exception {
	DBCollection collection = mongoTemplate.getCollection("teams");
	DBObject query = BasicDBObjectBuilder.start()
		.add("name", "VoxlrAdmin").get();
	DBObject update = BasicDBObjectBuilder.start()
				.add("companyId", companyId)
				.add("name", "VoxlrAdmin").get();
	WriteResult result = collection.update(query, update, true, false);
	if (result.getN() != 1) {
	    throw new Exception("Unable to create admin team");
	}
	teamId = collection.findOne(query).get("_id").toString();
    }
    
    @ChangeSet(order = "0003", id = "createSuperAdmin", author = "mgagliardo", runAlways = true)
    public void createSuperAdmin(MongoTemplate mongoTemplate) throws Exception {
	DBCollection collection = mongoTemplate.getCollection("users");
	DBObject query = BasicDBObjectBuilder.start()
				.add("email", "admin@getvoxlr.com").get();
	DBObject update = BasicDBObjectBuilder.start()
				.add("email", "admin@getvoxlr.com")
				.add("firstName", "Michael")
				.add("lastName", "Gagliardo")
				.add("companyId", companyId)
				.add("teamId", teamId)
				.add("password", bCryptPasswordEncoder.encode("V0xlrAdmin"))
				.add("createDate", new Date())
				.add("isDeleted", false)
				.add("role", UserRole.SUPER_ADMIN.toString()).get();
	WriteResult result = collection.update(query, update, true, false);
	if (result.getN() != 1) {
	    throw new Exception("Unable to create super admin");
	}
    }
}

package com.voxlr.marmoset.changelogs;

import java.util.Arrays;
import java.util.Date;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.voxlr.marmoset.auth.UserRole;

@ChangeLog(order = "0001")
public class DBM_001_InitializeVoxlr {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    
    @ChangeSet(order = "0001", id = "createSuperAdmin", author = "mgagliardo", runAlways = true)
    public void createSuperAdmin(MongoTemplate mongoTemplate) throws Exception {
	DBCollection collection = mongoTemplate.getCollection("users");
	DBObject query = BasicDBObjectBuilder.start()
				.add("email", "admin@voxlr.com").get();
	DBObject update = BasicDBObjectBuilder.start()
				.add("email", "admin@voxlr.com")
				.add("firstName", "Michael")
				.add("lastName", "Gagliardo")
				.add("companyId", "-1")
				.add("teamId", "-1")
				.add("password", bCryptPasswordEncoder.encode("V0xlrAdmin"))
				.add("createDate", new Date())
				.add("role", UserRole.SUPER_ADMIN.toString()).get();
	WriteResult result = collection.update(query, update, true, false);
	if (result.getN() != 1) {
	    throw new Exception("Unable to create super admin");
	}
    }
}

package com.voxlr.marmoset.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.result.UpdateResult;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ChangeLog(order = "0001")
public class DBM_001_InitializeVoxlr {

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  private String companyId;
  private String teamId;

  @ChangeSet(order = "0001", id = "createAdminCompany", author = "mgagliardo", runAlways = true)
  public void createAdminCompany(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("name").is("VoxlrAdmin"));

    UpdateResult result =
        mongoTemplate.upsert(query, new Update().set("name", "VoxlrAdmin"), Company.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create admin company");
    }

    companyId = mongoTemplate.findOne(query, Company.class).getId().toString();
  }

  @ChangeSet(order = "0002", id = "createAdminTeam", author = "mgagliardo", runAlways = true)
  public void createAdminTeam(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("name").is("VoxlrAdmin"));

    UpdateResult result =
        mongoTemplate.upsert(
            query,
            new Update().set("companyId", "companyId").set("name", "VoxlrAdmin"),
            Team.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create admin team");
    }
    teamId = mongoTemplate.findOne(query, Team.class).getId().toString();
  }

  @ChangeSet(order = "0003", id = "createSuperAdmin", author = "mgagliardo", runAlways = true)
  public void createSuperAdmin(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("email").is("admin@getvoxlr.com"));

    UpdateResult result =
        mongoTemplate.upsert(
            query,
            new Update()
                .set("email", "admin@getvoxlr.com")
                .set("firstName", "Michael")
                .set("lastName", "Gagliardo")
                .set("companyId", companyId)
                .set("teamId", teamId)
                .set("password", bCryptPasswordEncoder.encode("V0xlrAdmin"))
                .set("createDate", new DateTime())
                .set("isInactive", false)
                .set("role", UserRole.SUPER_ADMIN.toString()),
            User.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create super admin");
    }
  }
}

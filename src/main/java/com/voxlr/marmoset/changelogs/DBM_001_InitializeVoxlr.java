package com.voxlr.marmoset.changelogs;

import static com.voxlr.marmoset.model.CallOutcome.WON;
import static com.voxlr.marmoset.util.ListUtils.listOf;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.result.UpdateResult;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.Call.Statistic;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ChangeLog(order = "0001")
public class DBM_001_InitializeVoxlr {

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  private ObjectId companyId;
  private ObjectId teamId;
  private ObjectId superUserId;

  @ChangeSet(order = "0001", id = "createAdminCompany", author = "mgagliardo", runAlways = true)
  public void createAdminCompany(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("name").is("VoxlrAdmin"));

    UpdateResult result =
        mongoTemplate.upsert(query, new Update().set("name", "VoxlrAdmin"), Company.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create admin company");
    }

    companyId = mongoTemplate.findOne(query, Company.class).getId();
  }

  @ChangeSet(order = "0002", id = "createAdminTeam", author = "mgagliardo", runAlways = true)
  public void createAdminTeam(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("name").is("VoxlrAdmin"));

    UpdateResult result =
        mongoTemplate.upsert(
            query,
            new Update().set("companyId", companyId).set("name", "VoxlrAdmin"),
            Team.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create admin team");
    }
    teamId = mongoTemplate.findOne(query, Team.class).getId();
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
    superUserId =  mongoTemplate.findOne(query, User.class).getId();
  }

  @ChangeSet(order = "0004", id = "createFixtureCall1", author = "mgagliardo", runAlways = true)
  public void createFixtureCall1(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("callSid").is("1234567"));

    UpdateResult result =
        mongoTemplate.upsert(
            query,
            new Update()
                .set("callSid", "1234567")
                .set("transcriptionId", "1234567")
                .set("userId", superUserId)
                .set("companyId", companyId)
                .set("teamId", teamId)
                .set("empNum", new PhoneNumberHolder("+17047705326"))
                .set("custNum", new PhoneNumberHolder("+17047705326"))
                .set("createDate", new DateTime())
                .set("outcome", WON)
                .set("analyzed", true)
                .set("callStrategy", CallStrategy.builder().phrases(
                    listOf(
                        "Hello",
                        "Goodbye"
                    )
                ).build())
                .set("stats", Statistic.builder()
                    .duration(10000)
                    .totalTalkTime(10000)
                    .customerTalkTime(5000)
                    .employeeTalkTime(5000)
                    .build())
                .set("analysis", Analysis.builder()
                  .detectionRatio(0.5)
                  .detectedPhraseCount(1)
                  .build()),
            Call.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create call");
    }
  }

  @ChangeSet(order = "0005", id = "createFixtureCall2", author = "mgagliardo", runAlways = true)
  public void createFixtureCall2(MongoTemplate mongoTemplate) throws Exception {
    Query query = Query.query(Criteria.where("callSid").is("7654321"));

    UpdateResult result =
        mongoTemplate.upsert(
            query,
            new Update()
                .set("callSid", "7654321")
                .set("transcriptionId", "7654321")
                .set("userId", superUserId)
                .set("companyId", companyId)
                .set("teamId", teamId)
                .set("empNum", new PhoneNumberHolder("+17047705326"))
                .set("custNum", new PhoneNumberHolder("+17047705326"))
                .set("createDate", new DateTime())
                .set("outcome", WON)
                .set("analyzed", true)
                .set("callStrategy", CallStrategy.builder().phrases(
                    listOf(
                        "Hello",
                        "Goodbye"
                    )
                ).build())
                .set("stats", Statistic.builder()
                    .duration(12000)
                    .totalTalkTime(8000)
                    .customerTalkTime(5000)
                    .employeeTalkTime(3000)
                    .build())
                .set("analysis", Analysis.builder()
                    .detectionRatio(1.0)
                    .detectedPhraseCount(2)
                    .build()),
            Call.class);
    if (!(result.getUpsertedId() != null || result.getMatchedCount() == 1)) {
      throw new Exception("Unable to create call");
    }
  }
}

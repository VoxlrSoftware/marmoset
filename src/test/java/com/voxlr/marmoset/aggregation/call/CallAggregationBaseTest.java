package com.voxlr.marmoset.aggregation.call;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createEntity;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.aggregation.CallAggregation;
import com.voxlr.marmoset.model.CallOutcome;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.Call.Statistic;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.test.DataTest;

public abstract class CallAggregationBaseTest extends DataTest {
    public static final int DEFAULT_DURATION = 10;
    public static final int DEFAULT_TALK_TIME = 10000;
    public static final int DEFAULT_CUSTOMER_TALK_TIME = 5000;
    public static final int DEFAULT_EMPLOYEE_TALK_TIME = 5000;
    public static final int DEFAULT_PHRASE_COUNT = 2;
    public static final double DEFAULT_DETECTION_RATIO = 0.5;
    
    
    @Autowired
    MongoTemplate mongoTemplate;
    
    Company mockCompany;
    Team mockTeam;
    User mockUser;
    CallAggregation callAggregation;
    
    @Override
    public void beforeTest() {
	mockCompany = createCompany("Test Company");
	mockTeam = createTeam(mockCompany.getId(), "Test Team");
	mockUser = createUser(mockCompany.getId(), mockTeam.getId());
	
	persistenceUtils.save(mockCompany, mockTeam, mockUser);
	
	callAggregation = aCallAggregation(mongoTemplate);
    }
    
    public Call createCall(DateTime createDate) {
	Call call = createEntity(Call.builder()
		.companyId(mockCompany.getId())
		.userId(mockUser.getId())
		.hasBeenAnalyzed(true)
		.callOutcome(CallOutcome.WON)
		.callStrategy(new CallStrategy("Test Strategy", newArrayList()))
		.statistics(Statistic.builder()
			.duration(DEFAULT_DURATION)
			.totalTalkTime(DEFAULT_TALK_TIME)
			.customerTalkTime(DEFAULT_CUSTOMER_TALK_TIME)
			.employeeTalkTime(DEFAULT_EMPLOYEE_TALK_TIME).build())
		.analysis(Analysis.builder()
			.detectionRatio(DEFAULT_DETECTION_RATIO)
			.detectedPhraseCount(DEFAULT_PHRASE_COUNT).build())
		.build());
	call.setCreateDate(createDate);
	return call;
    }
}

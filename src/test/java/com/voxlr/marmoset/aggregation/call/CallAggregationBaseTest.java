package com.voxlr.marmoset.aggregation.call;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createEntity;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.aggregation.CallAggregation;
import com.voxlr.marmoset.model.CallOutcome;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.Call.Statistic;
import com.voxlr.marmoset.test.DataTest;

public abstract class CallAggregationBaseTest extends DataTest {
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
    
    public Call createCall(Date createDate) {
	Call call = createEntity(Call.builder()
		.companyId(mockCompany.getId())
		.userId(mockUser.getId())
		.hasBeenAnalyzed(true)
		.callOutcome(CallOutcome.WON)
		.callStrategy(new CallStrategy("Test Strategy", newArrayList()))
		.statistics(Statistic.builder()
			.duration(10)
			.totalTalkTime(10000)
			.customerTalkTime(5000)
			.employeeTalkTime(5000).build())
		.analysis(Analysis.builder()
			.detectionRatio(0.5)
			.detectedPhraseCount(2).build())
		.build());
	call.setCreateDate(createDate);
	return call;
    }
}

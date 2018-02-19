package com.voxlr.marmoset.aggregation.call;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;
import static com.voxlr.marmoset.util.AssertUtils.containsMatch;
import static com.voxlr.marmoset.util.EntityTestUtils.createCompany;
import static com.voxlr.marmoset.util.EntityTestUtils.createEntity;
import static com.voxlr.marmoset.util.EntityTestUtils.createTeam;
import static com.voxlr.marmoset.util.EntityTestUtils.createUser;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.aggregation.CallAggregation;
import com.voxlr.marmoset.model.CallOutcome;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.Call.Statistic;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.test.DataTest;

public class GetCallsByCompanyTest extends DataTest {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
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
    
    @Test
    public void getCallsByCompanyReturnsEmptyIfNoCallsExist() throws Exception {
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		new Date(),
		new Date(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(0L));
	assertThat(results.getContent().size(), is(0));
    }
    
    @Test
    public void shouldReturnValidListWithCorrectData() throws Exception {
	Date createDate = DateUtils.addDays(new Date(), -1);
	Call expected = createCall(createDate);
	persistenceUtils.save(expected);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		createDate,
		new Date(),
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
	
	CallAggregateDTO result = results.getContent().get(0);
	assertThat(result.getId(), is(expected.getId()));
	assertThat(result.getUserId(), is(expected.getUserId()));
	assertThat(result.getCompanyId(), is(expected.getCompanyId()));
	assertThat(result.getCallOutcome(), is(expected.getCallOutcome()));
	assertThat(result.getCallStrategyName(), is(expected.getCallStrategy().getName()));
	assertThat(result.getTotalTalkTime(), is(expected.getStatistics().getTotalTalkTime()));
	assertThat(result.getDuration(), is(expected.getStatistics().getDuration()));
	assertThat(result.getDetectedPhraseCount(), is(expected.getAnalysis().getDetectedPhraseCount()));
	assertThat(result.getDetectionRatio(), is(expected.getAnalysis().getDetectionRatio()));
	assertThat(result.getCustomerTalkRatio(), is(0.5));
    }
    
    @Test
    public void shouldOnlyReturnCallsWithinDateRange() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call outsideRange = createCall(new Date());
	
	persistenceUtils.save(withinRange, outsideRange);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
	CallAggregateDTO result = results.getContent().get(0);
	assertThat(result.getId(), is(withinRange.getId()));
    }
    
    @Test
    public void shouldReturnMultipleCallsWithinDateRange() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(2L));
	assertThat(results.getContent().size(), is(2));
	
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange.getId())));
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange2.getId())));
    }
    
    @Test
    public void shouldIgnoreCallsThatAreNotAnalyzed() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	withinRange2.setHasBeenAnalyzed(false);
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    @Test
    public void shouldIgnoreCallsThatAreNotInTheQueriedCompany() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	withinRange2.setCompanyId("123");
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    private Call createCall(Date createDate) {
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

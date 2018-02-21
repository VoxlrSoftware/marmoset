package com.voxlr.marmoset.aggregation.call;

import static com.voxlr.marmoset.util.AssertUtils.containsMatch;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class GetCallsTest extends CallAggregationBaseTest {
    
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
    public void getCallsByCompanyShouldReturnValidListWithCorrectData() throws Exception {
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
    public void getCallsByCompanyShouldOnlyReturnCallsWithinDateRange() {
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
    public void getCallsByCompanyShouldReturnMultipleCallsWithinDateRange() {
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
    public void getCallsByCompanyShouldIgnoreCallsThatAreNotAnalyzed() {
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
    public void getCallsByCompanyShouldIgnoreCallsThatAreNotInTheQueriedCompany() {
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
    
    @Test
    public void getCallsByUserReturnsEmptyIfNoCallsExist() throws Exception {
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		new Date(),
		new Date(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(0L));
	assertThat(results.getContent().size(), is(0));
    }
    
    @Test
    public void getCallsByUserShouldReturnValidListWithCorrectData() throws Exception {
	Date createDate = DateUtils.addDays(new Date(), -1);
	Call expected = createCall(createDate);
	persistenceUtils.save(expected);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
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
    public void getCallsByUserShouldOnlyReturnCallsWithinDateRange() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call outsideRange = createCall(new Date());
	
	persistenceUtils.save(withinRange, outsideRange);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
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
    public void getCallsByUserShouldReturnMultipleCallsWithinDateRange() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
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
    public void getCallsByUserShouldIgnoreCallsThatAreNotAnalyzed() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	withinRange2.setHasBeenAnalyzed(false);
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    @Test
    public void getCallsByUserShouldIgnoreCallsThatAreNotInTheQueriedUser() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	Call withinRange = createCall(DateUtils.addDays(startDate, 1));
	Call withinRange2 = createCall(DateUtils.addDays(startDate, 2));
	withinRange2.setUserId("123");
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
}

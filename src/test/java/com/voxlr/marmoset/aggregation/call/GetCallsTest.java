package com.voxlr.marmoset.aggregation.call;

import static com.voxlr.marmoset.util.AssertUtils.containsMatch;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.voxlr.marmoset.aggregation.field.CallAggregationFields;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class GetCallsTest extends CallAggregationBaseTest {
    
    @Test
    public void getCallsByCompanyReturnsEmptyIfNoCallsExist() throws Exception {
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		new DateTime(),
		new DateTime(),
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(0L));
	assertThat(results.getContent().size(), is(0));
    }
    
    @Test
    public void getCallsByCompanyShouldReturnValidListWithCorrectData() throws Exception {
	DateTime createDate = new DateTime().minusDays(1);
	Call expected = createCall(createDate);
	persistenceUtils.save(expected);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		createDate,
		new DateTime(),
		CallAggregationFields.CallField.getAll(),
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
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call outsideRange = createCall(new DateTime());
	
	persistenceUtils.save(withinRange, outsideRange);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
	CallAggregateDTO result = results.getContent().get(0);
	assertThat(result.getId(), is(withinRange.getId()));
    }
    
    @Test
    public void getCallsByCompanyShouldReturnMultipleCallsWithinDateRange() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(2L));
	assertThat(results.getContent().size(), is(2));
	
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange.getId())));
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange2.getId())));
    }
    
    @Test
    public void getCallsByCompanyShouldIgnoreCallsThatAreNotAnalyzed() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	withinRange2.setHasBeenAnalyzed(false);
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    @Test
    public void getCallsByCompanyShouldIgnoreCallsThatAreNotInTheQueriedCompany() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	withinRange2.setCompanyId("123");
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    @Test
    public void getCallsByUserReturnsEmptyIfNoCallsExist() throws Exception {
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		new DateTime(),
		new DateTime(),
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(0L));
	assertThat(results.getContent().size(), is(0));
    }
    
    @Test
    public void getCallsByUserShouldReturnValidListWithCorrectData() throws Exception {
	DateTime createDate = new DateTime().minusDays(1);
	Call expected = createCall(createDate);
	persistenceUtils.save(expected);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		createDate,
		new DateTime(),
		CallAggregationFields.CallField.getAll(),
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
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call outsideRange = createCall(new DateTime());
	
	persistenceUtils.save(withinRange, outsideRange);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
	CallAggregateDTO result = results.getContent().get(0);
	assertThat(result.getId(), is(withinRange.getId()));
    }
    
    @Test
    public void getCallsByUserShouldReturnMultipleCallsWithinDateRange() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);
	
	assertThat(results.getTotalElements(), is(2L));
	assertThat(results.getContent().size(), is(2));
	
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange.getId())));
	assertThat(results.getContent(), containsMatch(result -> result.getId().equals(withinRange2.getId())));
    }
    
    @Test
    public void getCallsByUserShouldIgnoreCallsThatAreNotAnalyzed() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	withinRange2.setHasBeenAnalyzed(false);
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
    
    @Test
    public void getCallsByUserShouldIgnoreCallsThatAreNotInTheQueriedUser() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	Call withinRange = createCall(startDate.plusDays(1));
	Call withinRange2 = createCall(startDate.plusDays(2));
	withinRange2.setUserId("123");
	
	persistenceUtils.save(withinRange, withinRange2);
	
	Page<CallAggregateDTO> results = callAggregation.getCallsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		CallAggregationFields.CallField.getAll(),
		PageRequest.of(0, 20)
	);

	assertThat(results.getTotalElements(), is(1L));
	assertThat(results.getContent().size(), is(1));
    }
}

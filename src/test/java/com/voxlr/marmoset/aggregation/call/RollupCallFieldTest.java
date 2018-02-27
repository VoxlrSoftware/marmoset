package com.voxlr.marmoset.aggregation.call;

import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class RollupCallFieldTest extends CallAggregationBaseTest {
    
    @Test
    public void rollupCallFieldHandlesEmptyDataSet() {
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		new DateTime(),
		new DateTime(),
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO, is(notNullValue()));
	assertThat(resultDTO.size(), is(0));
    }
    
    @Test
    public void rollupCallFieldReturnsValidValue() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate = endDate.minusDays(6);
	
	Call call1 = createCall(aggDate);
	Call call2 = createCall(aggDate);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result = resultDTO.get(0);
	assertThat(result.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(6000.0));
	assertThat(result.getTimestamp(), equalTo(aggDate));
    }
    
    @Test
    public void rollupCallFieldHandlesMultipleDates() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate1 = endDate.minusDays(6);
	DateTime aggDate2 = aggDate1.plusDays(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	assertThat(result1.getTimestamp(), equalTo(aggDate1));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
	
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result2.getTimestamp(), equalTo(aggDate2));
	assertThat(result2.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldHandlesMultipleFields() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate = endDate.minusDays(6);
	
	Call call1 = createCall(aggDate);
	Call call2 = createCall(aggDate);
	call2.getStatistics().setTotalTalkTime(2000);
	call2.getStatistics().setDuration(2);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME, CallAggregationField.DURATION)
	);
	
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result = resultDTO.get(0);
	assertThat(result.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(6000.0));
	assertThat(result.getResult().get(CallAggregationField.DURATION.get()), is(6.0));
	assertThat(result.getTimestamp(), equalTo(aggDate));
    }
    
    @Test
    public void rollupCallFieldOnlyIncludesInDateRange() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate1 = endDate.minusDays(6);
	DateTime aggDate2 = startDate.minusDays(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result = resultDTO.get(0);
	assertThat(result.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
	assertThat(result.getTimestamp(), equalTo(aggDate1));
    }
    
    @Test
public void rollupCallFieldHourly() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusHours(7);
	DateTime aggDate1 = endDate.minusHours(6);
	DateTime aggDate2 = aggDate1.plusHours(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		RollupCadence.HOURLY,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	assertThat(result1.getTimestamp(), equalTo(aggDate1));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
	
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result2.getTimestamp(), equalTo(aggDate2));
	assertThat(result2.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldMonthly() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusMonths(7);
	DateTime aggDate1 = endDate.minusMonths(6);
	DateTime aggDate2 = aggDate1.plusMonths(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		RollupCadence.MONTHLY,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getTimestamp(), equalTo(getStartOfMonth(aggDate1)));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
	
	assertThat(result2.getTimestamp(), equalTo(getStartOfMonth(aggDate2)));
	assertThat(result2.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldYearly() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusYears(7);
	DateTime aggDate1 = endDate.minusYears(6);
	DateTime aggDate2 = aggDate1.plusYears(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		RollupCadence.YEARLY,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getTimestamp(), equalTo(getStartOfYear(aggDate1)));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
	
	assertThat(result2.getTimestamp(), equalTo(getStartOfYear(aggDate2)));
	assertThat(result2.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldByCompanyOnlyIncludesSameCompany() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate1 = endDate.minusDays(6);
	DateTime aggDate2 = aggDate1.plusDays(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.setCompanyId("123");
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallFieldByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		RollupCadence.DAILY,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result1 = resultDTO.get(0);
	assertThat(result1.getTimestamp(), equalTo(aggDate1));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
    }
    
    @Test
    public void rollupCallFieldByUserOnlyIncludesSameUser() {
	DateTime endDate = getInitialDate();
	DateTime startDate = endDate.minusDays(7);
	DateTime aggDate1 = endDate.minusDays(6);
	DateTime aggDate2 = aggDate1.plusDays(1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.setUserId("123");
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallFieldByUser(
		mockUser.getId(),
		startDate,
		endDate,
		RollupCadence.DAILY,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result1 = resultDTO.get(0);
	assertThat(result1.getTimestamp(), equalTo(aggDate1));
	assertThat(result1.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), is(10000.0));
    }
    
    private DateTime getStartOfYear(DateTime dateTime) {
	return clearMonth(clearDay(clearTime(dateTime)));
    }
    
    private DateTime getStartOfMonth(DateTime dateTime) {
	return clearDay(clearTime(dateTime));
    }
    
    private DateTime getInitialDate() {
	return clearTime(new DateTime(DateTimeZone.UTC));
    }
    
    private DateTime clearMonth(DateTime dateTime) {
	return dateTime.withMonthOfYear(1);
    }
    
    private DateTime clearDay(DateTime dateTime) {
	return dateTime.withDayOfMonth(1);
    }
    
    private DateTime clearTime(DateTime dateTime) {
	return dateTime.withTime(0, 0, 0, 0);
    }
}

package com.voxlr.marmoset.aggregation.call;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class RollupCallFieldTest extends CallAggregationBaseTest {
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    @Test
    public void rollupCallFieldHandlesEmptyDataSet() {
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		new Date(),
		new Date(),
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO, is(notNullValue()));
	assertThat(resultDTO.size(), is(0));
    }
    
    @Test
    public void rollupCallFieldReturnsValidValue() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addDays(endDate, -7);
	Date aggDate = DateUtils.addDays(endDate, -6);
	
	Call call1 = createCall(aggDate);
	Call call2 = createCall(aggDate);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result = resultDTO.get(0);
	assertThat(result.getResult(), is(6000.0));
	assertThat(result.getName(), equalTo(formatter.format(aggDate)));
    }
    
    @Test
    public void rollupCallFieldHandlesMultipleDates() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addDays(endDate, -7);
	Date aggDate1 = DateUtils.addDays(endDate, -6);
	Date aggDate2 = DateUtils.addDays(aggDate1, 1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getName(), equalTo(formatter.format(aggDate1)));
	assertThat(result1.getResult(), is(10000.0));
	
	assertThat(result2.getName(), equalTo(formatter.format(aggDate2)));
	assertThat(result2.getResult(), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldOnlyIncludesInDateRange() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addDays(endDate, -7);
	Date aggDate1 = DateUtils.addDays(endDate, -6);
	Date aggDate2 = DateUtils.addDays(startDate, -1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.size(), is(1));
	
	RollupResultDTO result = resultDTO.get(0);
	assertThat(result.getResult(), is(10000.0));
	assertThat(result.getName(), equalTo(formatter.format(aggDate1)));
    }
    
    @Test
    public void rollupCallFieldHourly() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addHours(endDate, -7);
	Date aggDate1 = DateUtils.addHours(endDate, -6);
	Date aggDate2 = DateUtils.addHours(aggDate1, 1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME,
		RollupCadence.HOURLY
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getName(), equalTo(formatter.format(aggDate1)));
	assertThat(result1.getResult(), is(10000.0));
	
	assertThat(result2.getName(), equalTo(formatter.format(aggDate2)));
	assertThat(result2.getResult(), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldMonthly() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addMonths(endDate, -7);
	Date aggDate1 = DateUtils.addMonths(endDate, -6);
	Date aggDate2 = DateUtils.addMonths(aggDate1, 1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME,
		RollupCadence.MONTHLY
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getName(), equalTo(formatter.format(getStartOfMonth(aggDate1))));
	assertThat(result1.getResult(), is(10000.0));
	
	assertThat(result2.getName(), equalTo(formatter.format(getStartOfMonth(aggDate2))));
	assertThat(result2.getResult(), is(2000.0));
    }
    
    @Test
    public void rollupCallFieldYearly() {
	Date endDate = getInitialDate();
	Date startDate = DateUtils.addYears(endDate, -7);
	Date aggDate1 = DateUtils.addYears(endDate, -6);
	Date aggDate2 = DateUtils.addYears(aggDate1, 1);
	
	Call call1 = createCall(aggDate1);
	Call call2 = createCall(aggDate2);
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	List<RollupResultDTO> resultDTO = callAggregation.rollupCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME,
		RollupCadence.YEARLY
	);
	assertThat(resultDTO.size(), is(2));
	
	RollupResultDTO result1 = resultDTO.get(0);
	RollupResultDTO result2 = resultDTO.get(1);
	assertThat(result1.getName(), equalTo(formatter.format(getStartOfYear(aggDate1))));
	assertThat(result1.getResult(), is(10000.0));
	
	assertThat(result2.getName(), equalTo(formatter.format(getStartOfYear(aggDate2))));
	assertThat(result2.getResult(), is(2000.0));
    }
    
    private Date getStartOfYear(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	clearTime(calendar);
	clearDay(calendar);
	clearMonth(calendar);
	return calendar.getTime();
    }
    
    private Date getStartOfMonth(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	clearTime(calendar);
	clearDay(calendar);
	return calendar.getTime();
    }
    
    private Date getInitialDate() {
	Calendar calendar = Calendar.getInstance();
	clearTime(calendar);
	return calendar.getTime();
    }
    
    private void clearMonth(Calendar calendar) {
	calendar.set(Calendar.MONTH, 0);
    }
    
    private void clearDay(Calendar calendar) {
	calendar.set(Calendar.DAY_OF_MONTH, 1);
    }
    
    private Calendar clearTime(Calendar calendar) {
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	calendar.set(Calendar.MILLISECOND, 0);
	return calendar;
    }
}

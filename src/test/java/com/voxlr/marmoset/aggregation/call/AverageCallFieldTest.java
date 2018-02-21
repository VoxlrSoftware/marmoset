package com.voxlr.marmoset.aggregation.call;

import static com.voxlr.marmoset.aggregation.CallAggregation.AVG_FIELDS_WHITE_LIST;
import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class AverageCallFieldTest extends CallAggregationBaseTest {
    @Test
    public void averageCallFieldReturnsValidDefaultValue() throws Exception {
	AVG_FIELDS_WHITE_LIST.stream().forEach(field -> {
	    wrapNoException(() -> {
		RollupResultDTO resultDTO = callAggregation.averageCallField(
			Criteria.where("id").exists(true),
			new Date(),
			new Date(),
			field
		);
		
		assertThat(resultDTO.getResult(), is(field.getDefaultValue()));
	    });
	});
    }
    
    @Test
    public void averageCallFieldReturnsValidValue() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call1 = createCall(DateUtils.addDays(startDate, 1));
	Call call2 = createCall(DateUtils.addDays(startDate, 1));
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.getResult(), equalTo(6000.0));
    }
    
    @Test
    public void averageCallFieldIgnoresOutOfDateRange() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call1 = createCall(DateUtils.addDays(startDate, 1));
	Call call2 = createCall(DateUtils.addDays(startDate, -1));
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallField(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.getResult(), equalTo(10000.0));
    }
    
    @Test
    public void averageCallFieldAcceptsValidField() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call = createCall(DateUtils.addDays(startDate, 1));
	persistenceUtils.save(call);
	
	AVG_FIELDS_WHITE_LIST.stream().forEach(field -> {
	    wrapNoException(() -> {
		callAggregation.averageCallField(
			Criteria.where("id").exists(true),
			startDate,
			endDate,
			field
		);
	    });
	});
    }
    
    @Test
    public void averageCallFieldThrowsInvalidField() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call = createCall(DateUtils.addDays(startDate, 1));
	persistenceUtils.save(call);
	
	Set<CallAggregationField> fields = new HashSet<CallAggregationField>(listOf(CallAggregationField.values()));
	fields.removeAll(AVG_FIELDS_WHITE_LIST);
	
	fields.stream().forEach(field -> {
	    wrapAssertException(() -> {
		callAggregation.averageCallField(
			Criteria.where("id").exists(true),
			startDate,
			endDate,
			field
		);
	    }, IllegalArgumentException.class);
	});
    }
    
    @Test
    public void averageCallFieldByCompanyOnlyAveragesSameCompany() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call1 = createCall(DateUtils.addDays(startDate, 1));
	Call call2 = createCall(DateUtils.addDays(startDate, 1));
	call2.setCompanyId("123");
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFieldByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.getResult(), equalTo(10000.0));
    }
    
    @Test
    public void averageCallFieldByUserOnlyAveragesSameUser() {
	Date startDate = DateUtils.addDays(new Date(), -7);
	Date endDate = DateUtils.addDays(new Date(), -1);
	
	Call call1 = createCall(DateUtils.addDays(startDate, 1));
	Call call2 = createCall(DateUtils.addDays(startDate, 1));
	call2.setUserId("123");
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFieldByUser(
		mockUser.getId(),
		startDate,
		endDate,
		CallAggregationField.TOTAL_TALK_TIME
	);
	
	assertThat(resultDTO.getResult(), equalTo(10000.0));
    }
}

package com.voxlr.marmoset.aggregation.call;

import static com.voxlr.marmoset.aggregation.CallAggregation.AVG_FIELDS_WHITE_LIST;
import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class AverageCallFieldTest extends CallAggregationBaseTest {
    @Test
    public void averageCallFieldReturnsValidDefaultValue() throws Exception {
	RollupResultDTO resultDTO = callAggregation.averageCallFields(
		Criteria.where("id").exists(true),
		new DateTime(),
		new DateTime(),
		new ArrayList<CallAggregationField>(AVG_FIELDS_WHITE_LIST)
	);
	
	assertThat(resultDTO.getResult().size(), is(AVG_FIELDS_WHITE_LIST.size()));
	
	Map<String, Object> result = resultDTO.getResult();
	
	AVG_FIELDS_WHITE_LIST.stream().forEach(field -> {
	    assertThat(result, hasKey(field.get()));
	    assertThat(result.get(field.get()), is(field.getDefaultValue()));
	});
    }
    
    @Test
    public void averageCallFieldReturnsValidValue() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);
	
	Call call1 = createCall(startDate.plusDays(1));
	Call call2 = createCall(startDate.plusDays(1));
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFields(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);

	assertThat(resultDTO.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), equalTo(6000.0));
    }
    
    @Test
    public void averageCallFieldIgnoresOutOfDateRange() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);

	Call call1 = createCall(startDate.plusDays(1));
	Call call2 = createCall(startDate.minusDays(1));
	call2.getStatistics().setTotalTalkTime(2000);
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFields(
		Criteria.where("id").exists(true),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), equalTo(10000.0));
    }
    
    @Test
    public void averageCallFieldAcceptsValidField() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);

	Call call = createCall(startDate.plusDays(1));
	persistenceUtils.save(call);
	
	AVG_FIELDS_WHITE_LIST.stream().forEach(field -> {
	    wrapNoException(() -> {
		callAggregation.averageCallFields(
			Criteria.where("id").exists(true),
			startDate,
			endDate,
			listOf(field)
		);
	    });
	});
    }
    
    @Test
    public void averageCallFieldThrowsInvalidField() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);

	Call call = createCall(startDate.plusDays(1));
	persistenceUtils.save(call);
	
	Set<CallAggregationField> fields = new HashSet<CallAggregationField>(listOf(CallAggregationField.values()));
	fields.removeAll(AVG_FIELDS_WHITE_LIST);
	
	fields.stream().forEach(field -> {
	    wrapAssertException(() -> {
		callAggregation.averageCallFields(
			Criteria.where("id").exists(true),
			startDate,
			endDate,
			listOf(field)
		);
	    }, IllegalArgumentException.class);
	});
    }
    
    @Test
    public void averageCallFieldByCompanyOnlyAveragesSameCompany() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);

	Call call1 = createCall(startDate.plusDays(1));
	Call call2 = createCall(startDate.plusDays(1));
	call2.setCompanyId("123");
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFieldsByCompany(
		mockCompany.getId(),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), equalTo(10000.0));
    }
    
    @Test
    public void averageCallFieldByUserOnlyAveragesSameUser() {
	DateTime startDate = new DateTime().minusDays(7);
	DateTime endDate = new DateTime().minusDays(1);

	Call call1 = createCall(startDate.plusDays(1));
	Call call2 = createCall(startDate.plusDays(1));
	call2.setUserId("123");
	persistenceUtils.save(call1, call2);
	
	RollupResultDTO resultDTO = callAggregation.averageCallFieldsByUser(
		mockUser.getId(),
		startDate,
		endDate,
		listOf(CallAggregationField.TOTAL_TALK_TIME)
	);
	
	assertThat(resultDTO.getResult().get(CallAggregationField.TOTAL_TALK_TIME.get()), equalTo(10000.0));
    }
}

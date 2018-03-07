package com.voxlr.marmoset.repositories;

import static com.voxlr.marmoset.aggregation.NewCallAggregation.aNewCallAggregation;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.field.CallAggFields.CallField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;

public class CustomizedCallRepositoryImpl implements CustomizedCallRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public Page<CallAggregateDTO> getCallsByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable) {
	return aNewCallAggregation(mongoTemplate)
		.getCallsByCompany(companyId, startDate, endDate, fields, pageable);
    }

    @Override
    public Page<CallAggregateDTO> getCallsByUser(String userId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable) {
	return aNewCallAggregation(mongoTemplate)
		.getCallsByUser(userId, startDate, endDate, fields, pageable);
    }

    @Override
    public RollupResultDTO averageCallFieldByUser(String userId, DateTime startDate, DateTime endDate,
	    List<CallField> fields) {
	return aNewCallAggregation(mongoTemplate)
		.averageCallFieldsByUser(userId, startDate, endDate, fields);
    }

    @Override
    public RollupResultDTO averageCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate,
	    List<CallField> fields) {
	return aNewCallAggregation(mongoTemplate)
		.averageCallFieldsByCompany(companyId, startDate, endDate, fields);
    }

    @Override
    public List<RollupResultDTO> rollupCallFieldByUser(String userId, DateTime startDate, DateTime endDate,
	    RollupCadence cadence, List<CallField> fields) {
	return aNewCallAggregation(mongoTemplate)
		.rollupCallFieldByUser(userId, startDate, endDate, cadence, fields);
    }

    @Override
    public List<RollupResultDTO> rollupCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate,
	    RollupCadence cadence, List<CallField> fields) {
	return aNewCallAggregation(mongoTemplate)
		.rollupCallFieldByCompany(companyId, startDate, endDate, cadence, fields);
    }

}

package com.voxlr.marmoset.repositories;

import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;

public class CustomizedCallRepositoryImpl implements CustomizedCallRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public Page<CallAggregateDTO> getCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable) {
	return aCallAggregation(mongoTemplate)
		.getCallsByCompany(companyId, startDate, endDate, pageable);
    }

    @Override
    public Page<CallAggregateDTO> getCallsByUser(String userId, Date startDate, Date endDate, Pageable pageable) {
	return aCallAggregation(mongoTemplate)
		.getCallsByUser(userId, startDate, endDate, pageable);
    }

    @Override
    public RollupResultDTO averageCallFieldByUser(String userId, Date startDate, Date endDate,
	    CallAggregationField field) {
	return aCallAggregation(mongoTemplate)
		.averageCallFieldByUser(userId, startDate, endDate, field);
    }

    @Override
    public RollupResultDTO averageCallFieldByCompany(String companyId, Date startDate, Date endDate,
	    CallAggregationField field) {
	return aCallAggregation(mongoTemplate)
		.averageCallFieldByCompany(companyId, startDate, endDate, field);
    }

}

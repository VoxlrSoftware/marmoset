package com.voxlr.marmoset.repositories;

import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;

public class CustomizedCallRepositoryImpl implements CustomizedCallRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public Page<CallAggregateDTO> aggregateCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable) {
	return aCallAggregation(mongoTemplate)
		.aggregateCallsByCompany(companyId, startDate, endDate, pageable);
    }

}

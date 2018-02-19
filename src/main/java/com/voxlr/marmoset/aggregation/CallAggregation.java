package com.voxlr.marmoset.aggregation;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class CallAggregation extends AbstractAggregation {
    
    public CallAggregation(MongoTemplate mongoTemplate) {
	super(mongoTemplate);
    }
    
    public static CallAggregation aCallAggregation(MongoTemplate mongoTemplate) {
	return new CallAggregation(mongoTemplate);
    }

    public Page<CallAggregateDTO> aggregateCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable) {
	MatchOperation matchOperation = match(Criteria
		.where("companyId").is(companyId)
		.and("createDate").gte(startDate).lte(endDate)
		.and("hasBeenAnalyzed").is(true)
	);
	
	int totalCount = doCount(Call.class, matchOperation);
	if (totalCount == 0) {
	    return new PageImpl<CallAggregateDTO>(newArrayList(), pageable, totalCount);
	}
	
	ProjectionOperation projectionOperation = project("createDate", "callOutcome", "companyId", "userId")
		.and("callStrategy.name").as("callStrategyName")
		.and("statistics.totalTalkTime").as("totalTalkTime")
		.and("statistics.duration").as("duration")
		.and("analysis.detectedPhraseCount").as("detectedPhraseCount")
		.and("analysis.detectionRatio").as("detectionRatio")
		.andExpression("stats.totalTalkTime > 0 ? (stats.customerTalkTime / stats.totalTalkTime) : 0").as("customerTalkRatio");
	
	SortOperation sortOperation = sort(pageable.getSort());
	
	TypedAggregation<Call> aggregation = newAggregation(
		Call.class,
		matchOperation,
		sortOperation,
		skip((long) pageable.getPageNumber() * pageable.getPageSize()),
		limit(pageable.getPageSize()),
		projectionOperation
		);
	
	AggregationResults<CallAggregateDTO> results = mongo().aggregate(aggregation, CallAggregateDTO.class);
	
	
	return new PageImpl<CallAggregateDTO>(results.getMappedResults(), pageable, totalCount);
    }
}

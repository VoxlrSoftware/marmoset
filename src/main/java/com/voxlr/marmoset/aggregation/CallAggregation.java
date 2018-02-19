package com.voxlr.marmoset.aggregation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class CallAggregation extends AbstractAggregation<Call> {
    
    private final ProjectionOperation callAggregateProjection = project("createDate", "callOutcome", "companyId", "userId")
		.and("callStrategy.name").as("callStrategyName")
		.and("statistics.totalTalkTime").as("totalTalkTime")
		.and("statistics.duration").as("duration")
		.and("analysis.detectedPhraseCount").as("detectedPhraseCount")
		.and("analysis.detectionRatio").as("detectionRatio")
		.andExpression("cond(statistics.totalTalkTime > 0, statistics.customerTalkTime / statistics.totalTalkTime, 0)").as("customerTalkRatio");
    
    public CallAggregation(MongoTemplate mongoTemplate) {
	super(mongoTemplate, Call.class);
    }
    
    public static CallAggregation aCallAggregation(MongoTemplate mongoTemplate) {
	return new CallAggregation(mongoTemplate);
    }

    public Page<CallAggregateDTO> getCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable) {
	MatchOperation matchOperation = match(Criteria
		.where("companyId").is(companyId)
		.and("createDate").gte(startDate).lte(endDate)
		.and("hasBeenAnalyzed").is(true)
	);
	TypedAggregation<Call> aggregation = anAggregation()
		.append(matchOperation)
		.withPaging(pageable, sort(Direction.DESC, "createDate"))
		.append(callAggregateProjection)
		.build();

	return executePagedAggregation(matchOperation, pageable, aggregation, CallAggregateDTO.class);
    }
    
    public Page<CallAggregateDTO> getCallsByUser(String userId, Date startDate, Date endDate, Pageable pageable) {
	MatchOperation matchOperation = match(Criteria
		.where("userId").is(userId)
		.and("createDate").gte(startDate).lte(endDate)
		.and("hasBeenAnalyzed").is(true)
	);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(matchOperation)
		.withPaging(pageable, sort(Direction.DESC, "createDate"))
		.append(callAggregateProjection)
		.build();

	return executePagedAggregation(matchOperation, pageable, aggregation, CallAggregateDTO.class);
    }
}

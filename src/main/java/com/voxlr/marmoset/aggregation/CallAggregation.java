package com.voxlr.marmoset.aggregation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class CallAggregation extends AbstractAggregation<Call> {
    
    public static final Set<CallAggregationField> AVG_FIELDS_WHITE_LIST = 
	    new HashSet<>(Arrays.asList(
		    CallAggregationField.TOTAL_TALK_TIME,
		    CallAggregationField.DURATION,
		    CallAggregationField.DETECTED_PHRASE_COUNT,
		    CallAggregationField.DETECTION_RATIO,
		    CallAggregationField.CUSTOMER_TALK_RATIO
		));
    
    public enum CallAggregationField {
	CREATE_DATE("createDate"),
	CALL_OUTCOME("callOutcome"),
	COMPANY_ID("companyId"),
	USER_ID("userId"),
	CALL_STRATEGY_NAME("callStrategyName"),
	TOTAL_TALK_TIME("totalTalkTime", 0),
	DURATION("duration", 0.0),
	DETECTED_PHRASE_COUNT("detectedPhraseCount", 0),
	DETECTION_RATIO("detectionRatio", 0.0),
	CUSTOMER_TALK_RATIO("customerTalkRatio", 0.0)
	;
	
	private String name;
	private Object defaultValue = null;
	
	private CallAggregationField(String name) {
	    this.name = name;
	}
	
	private CallAggregationField(String name, Object defaultValue) {
	    this.name = name;
	    this.defaultValue = defaultValue;
	}
	
	public String get() {
	    return this.name;
	}
	
	public Object getDefaultValue() {
	    return this.defaultValue;
	}
    }
    
    private final Criteria hasBeenAnalyzed = Criteria.where("hasBeenAnalyzed").is(true);
    
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
    
    private Criteria getDateConstrained(Date startDate, Date endDate) {
	return Criteria.where("createDate").gte(startDate).lte(endDate);
    }
    
    private Criteria getUserConstrained(String userId) {
	return Criteria.where(CallAggregationField.USER_ID.get()).is(userId);
    }
    
    private Criteria getCompanyConstrained(String companyId) {
	return Criteria.where(CallAggregationField.COMPANY_ID.get()).is(companyId);
    }
    
    private Page<CallAggregateDTO> getCalls(Criteria scopeCriteria, Date startDate, Date endDate, Pageable pageable) {
	MatchOperation matchOperation = match(
		scopeCriteria
		.andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed)
	);
	TypedAggregation<Call> aggregation = anAggregation()
		.append(matchOperation)
		.withPaging(pageable, sort(Direction.DESC, "createDate"))
		.append(callAggregateProjection)
		.build();

	return executePagedAggregation(matchOperation, pageable, aggregation, CallAggregateDTO.class);
    }

    public Page<CallAggregateDTO> getCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable) {
	return getCalls(getCompanyConstrained(companyId), startDate, endDate, pageable);
    }
    
    public Page<CallAggregateDTO> getCallsByUser(String userId, Date startDate, Date endDate, Pageable pageable) {
	return getCalls(getUserConstrained(userId), startDate, endDate, pageable);
    }
    
    public RollupResultDTO averageCallField(Criteria scopeCriteria, Date startDate, Date endDate, CallAggregationField field) {
	if (!AVG_FIELDS_WHITE_LIST.contains(field)) {
	    throw new IllegalArgumentException("Field [" + field.get() + "] cannot be averaged");
	}
	
	MatchOperation matchOperation = match(
		scopeCriteria
		.andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed)
	);
	
	GroupOperation groupOperation = group().avg(field.get()).as("result");
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			callAggregateProjection,
			groupOperation
		)
		.build();
	
	RollupResultDTO resultDTO =  executeSingleAggregation(aggregation, RollupResultDTO.class);
	
	return resultDTO != null ? resultDTO : new RollupResultDTO(field.getDefaultValue());
    }
    
    public RollupResultDTO averageCallFieldByCompany(String companyId, Date startDate, Date endDate, CallAggregationField field) {
	return averageCallField(getCompanyConstrained(companyId), startDate, endDate, field);
    }
    
    public RollupResultDTO averageCallFieldByUser(String userId, Date startDate, Date endDate, CallAggregationField field) {
	return averageCallField(getUserConstrained(userId), startDate, endDate, field);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria scopeCriteria, Date startDate, Date endDate, CallAggregationField field) {
	return rollupCallField(scopeCriteria, startDate, endDate, field, RollupCadence.DAILY);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria scopeCriteria, Date startDate, Date endDate, CallAggregationField field, RollupCadence cadence) {
	if (!AVG_FIELDS_WHITE_LIST.contains(field)) {
	    throw new IllegalArgumentException("Field [" + field.get() + "] cannot be averaged");
	}
	
	MatchOperation matchOperation = match(
		scopeCriteria
		.andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed)
	);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			callAggregateProjection
				.and("createDate").dateAsFormattedString(cadence.getValue()).as("rollup"),
			group("rollup").avg(field.get()).as(RESULT),
			project("result").and(NAME).previousOperation(),
			sort(Direction.ASC, NAME)
		)
		.build();
	
	return executeAggregation(aggregation, RollupResultDTO.class);
    }
    
    public List<RollupResultDTO> rollupCallFieldByCompany(String companyId, Date startDate, Date endDate, CallAggregationField field, RollupCadence cadence) {
	return rollupCallField(getCompanyConstrained(companyId), startDate, endDate, field, cadence);
    }
    
    public List<RollupResultDTO> rollupCallFieldByUser(String userId, Date startDate, Date endDate, CallAggregationField field, RollupCadence cadence) {
	return rollupCallField(getUserConstrained(userId), startDate, endDate, field, cadence);
    }
}

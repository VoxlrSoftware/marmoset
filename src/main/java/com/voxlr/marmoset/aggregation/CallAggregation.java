package com.voxlr.marmoset.aggregation;

import static com.voxlr.marmoset.aggregation.operation.AddFieldsOperation.addFields;
import static com.voxlr.marmoset.aggregation.operation.ProjectFieldsOperation.projectFields;
import static com.voxlr.marmoset.aggregation.operation.SortFieldsOperation.sortFields;
import static com.voxlr.marmoset.util.ListUtils.mapList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.voxlr.marmoset.model.ConvertibleEnum;
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
		    CallAggregationField.CUSTOMER_TALK_RATIO,
		    CallAggregationField.CONVERSATION
		));
    
    public static enum CallAggregationField implements ConvertibleEnum {
	CREATE_DATE("createDate"),
	CALL_OUTCOME("callOutcome"),
	COMPANY_ID("companyId"),
	USER_ID("userId"),
	CALL_STRATEGY_NAME("callStrategyName"),
	TOTAL_TALK_TIME("totalTalkTime", 0),
	DURATION("duration", 0.0),
	DETECTED_PHRASE_COUNT("detectedPhraseCount", 0),
	DETECTION_RATIO("detectionRatio", 0.0),
	CUSTOMER_TALK_RATIO("customerTalkRatio", 0.0),
	CONVERSATION("conversation", 0),
	TOTAL_COUNT("totalCount", 0)
	;
	
	static Map<String, CallAggregationField> callAggregationFields;
	
	static {
	    callAggregationFields = Arrays.asList(CallAggregationField.values()).stream().collect(Collectors.toMap(CallAggregationField::get, Function.identity()));
	}

	@JsonCreator
	public static CallAggregationField fromString(String value) {
	    return callAggregationFields.get(value);
	}
	
	private String name;
	private Object defaultValue = null;
	
	private CallAggregationField(String name) {
	    this.name = name;
	}
	
	private CallAggregationField(String name, Object defaultValue) {
	    this.name = name;
	    this.defaultValue = defaultValue;
	}
	
	@JsonValue
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
		.andExpression("cond(statistics.totalTalkTime > 0, statistics.customerTalkTime / statistics.totalTalkTime, 0)").as("customerTalkRatio")
		.andExpression("cond(in(callOutcome, new String[]{'Lost', 'Won', 'Progress'}), 1, 0)").as("conversation");
    
    public CallAggregation(MongoTemplate mongoTemplate) {
	super(mongoTemplate, Call.class);
    }
    
    public static CallAggregation aCallAggregation(MongoTemplate mongoTemplate) {
	return new CallAggregation(mongoTemplate);
    }
    
    private Criteria getDateConstrained(DateTime startDate, DateTime endDate) {
	return Criteria.where("createDate").gte(startDate).lte(endDate);
    }
    
    private Criteria getUserConstrained(String userId) {
	return Criteria.where(CallAggregationField.USER_ID.get()).is(userId);
    }
    
    private Criteria getCompanyConstrained(String companyId) {
	return Criteria.where(CallAggregationField.COMPANY_ID.get()).is(companyId);
    }
    
    private Page<CallAggregateDTO> getCalls(Criteria scopeCriteria, DateTime startDate, DateTime endDate, Pageable pageable) {
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

    public Page<CallAggregateDTO> getCallsByCompany(String companyId, DateTime startDate, DateTime endDate, Pageable pageable) {
	return getCalls(getCompanyConstrained(companyId), startDate, endDate, pageable);
    }
    
    public Page<CallAggregateDTO> getCallsByUser(String userId, DateTime startDate, DateTime endDate, Pageable pageable) {
	return getCalls(getUserConstrained(userId), startDate, endDate, pageable);
    }
    
    public RollupResultDTO averageCallFields(Criteria scopeCriteria, DateTime startDate, DateTime endDate, List<CallAggregationField> fields) {
	List<String> averageFields = getAndValidateAggFields(fields);
	
	MatchOperation matchOperation = match(
		scopeCriteria
		.andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed)
	);
	
	GroupOperation groupOperation = groupAndAverage(null, fields);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			callAggregateProjection,
			groupOperation,
			addFields().withFields("result", averageFields).build(),
			projectFields("result").build()
		)
		.build();
	
	RollupResultDTO resultDTO =  executeSingleAggregation(aggregation, RollupResultDTO.class);
	
	return resultDTO != null ? resultDTO : new RollupResultDTO(getDefaultResult(fields));
    }
    
    public RollupResultDTO averageCallFieldsByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallAggregationField> fields) {
	return averageCallFields(getCompanyConstrained(companyId), startDate, endDate, fields);
    }
    
    public RollupResultDTO averageCallFieldsByUser(String userId, DateTime startDate, DateTime endDate, List<CallAggregationField> fields) {
	return averageCallFields(getUserConstrained(userId), startDate, endDate, fields);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria scopeCriteria, DateTime startDate, DateTime endDate, List<CallAggregationField> fields) {
	return rollupCallField(scopeCriteria, startDate, endDate, RollupCadence.DAILY, fields);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria scopeCriteria, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallAggregationField> fields) {
	List<String> averageFields = getAndValidateAggFields(fields);
	
	MatchOperation matchOperation = match(
		scopeCriteria
		.andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed)
	);
	
	GroupOperation groupOperation = groupAndAverage("rollup", fields);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			callAggregateProjection
				.and("createDate").dateAsFormattedString(cadence.getValue()).as("rollup"),
			groupOperation,
			addFields().withFields("result", averageFields).build(),
			projectFields("result").and(TIMESTAMP).previousOperation().build(),
			sortFields(Direction.ASC, TIMESTAMP).build()
		)
		.build();
	
	return executeAggregation(aggregation, RollupResultDTO.class);
    }
    
    public List<RollupResultDTO> rollupCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallAggregationField> fields) {
	return rollupCallField(getCompanyConstrained(companyId), startDate, endDate, cadence, fields);
    }
    
    public List<RollupResultDTO> rollupCallFieldByUser(String userId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallAggregationField> fields) {
	return rollupCallField(getUserConstrained(userId), startDate, endDate, cadence, fields);
    }
    
    private Map<String, Object> getDefaultResult(List<CallAggregationField> fields) {
	return fields.stream().collect(Collectors.toMap(CallAggregationField::get, CallAggregationField::getDefaultValue));
    }
    
    private GroupOperation groupAndAverage(String groupField, List<CallAggregationField> fields) {
	GroupOperation groupOperation = groupField != null ? group(groupField) : group();
	
	for(CallAggregationField field : fields) {
	    if (field.equals(CallAggregationField.CONVERSATION)) {
		groupOperation = groupOperation.sum(field.get()).as(field.get());
	    } else if (field.equals(CallAggregationField.TOTAL_COUNT)) {
		groupOperation = groupOperation.count().as(field.get());
	    } else {		
		groupOperation = groupOperation.avg(field.get()).as(field.get());
	    }
	}
	
	return groupOperation;
    }
    
    private List<String> getAndValidateAggFields(List<CallAggregationField> fields) {
	List<CallAggregationField> invalidFieldList = fields.stream().filter(field -> !AVG_FIELDS_WHITE_LIST.contains(field)).collect(Collectors.toList());
	if (invalidFieldList.size() > 0) {
	    String invalidFields = invalidFieldList.stream().map(CallAggregationField::get).collect(Collectors.joining(","));
	    throw new IllegalArgumentException("Field(s) [" + invalidFields + "] cannot be averaged");
	}
	
	return mapList(fields, CallAggregationField::get);
    }
}

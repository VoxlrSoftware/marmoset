package com.voxlr.marmoset.aggregation;

import static com.voxlr.marmoset.aggregation.field.AggregationField.getDefaults;
import static com.voxlr.marmoset.aggregation.operation.AddFieldsOperation.addFields;
import static com.voxlr.marmoset.aggregation.operation.ProjectFieldsOperation.projectFields;
import static com.voxlr.marmoset.aggregation.operation.SortFieldsOperation.sortFields;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static com.voxlr.marmoset.util.ListUtils.reduce;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import com.voxlr.marmoset.aggregation.field.CallAggFields;
import com.voxlr.marmoset.aggregation.field.CallAggFields.CallField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;
import com.voxlr.marmoset.model.persistence.Call;

public class NewCallAggregation extends AbstractAggregation<Call> {
    private final List<AggregationField> REQUIRED_FIELDS = listOf(
	    CallAggFields.field(CallField.USER_ID),
	    CallAggFields.field(CallField.COMPANY_ID),
	    CallAggFields.field(CallField.CREATE_DATE)
	);
    
    private final Criteria hasBeenAnalyzed = Criteria.where("hasBeenAnalyzed").is(true);
    
    public NewCallAggregation(MongoTemplate mongoTemplate) {
	super(mongoTemplate, Call.class);
    }
    public static NewCallAggregation aNewCallAggregation(MongoTemplate mongoTemplate) {
	return new NewCallAggregation(mongoTemplate);
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
    
    public List<AggregationField> getRequiredFields(List<CallField> fields) {
	Set<AggregationField> fieldSet = new HashSet<>();
	fieldSet.addAll(REQUIRED_FIELDS);
	fieldSet.addAll(fields.stream().map(CallAggFields::field).collect(Collectors.toList()));
	return new ArrayList<AggregationField>(fieldSet);
    }
    
    private Page<CallAggregateDTO> getCalls(Criteria matchCriteria, List<CallField> fields, Pageable pageable) {
	MatchOperation matchOperation = match(matchCriteria);
	ProjectionOperation projectionOperation = reduce(project(), getRequiredFields(fields), AggregationField::projectField);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(matchOperation)
		.withPaging(pageable, sort(Direction.DESC, "createDate"))
		.append(projectionOperation)
		.build();

	return executePagedAggregation(matchOperation, pageable, aggregation, CallAggregateDTO.class);
    }
    
    public Page<CallAggregateDTO> getCallsByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable) {
	Criteria matchCriteria = getCompanyConstrained(companyId).andOperator(getDateConstrained(startDate, endDate));
	
	return getCalls(matchCriteria, fields, pageable);
    }
    
    public Page<CallAggregateDTO> getCallsByUser(String userId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable) {
	Criteria matchCriteria = getUserConstrained(userId).andOperator(getDateConstrained(startDate, endDate));
	
	return getCalls(matchCriteria, fields, pageable);
    }
    
    public RollupResultDTO averageCallFields(Criteria matchCriteria, List<CallField> fields) {
	List<AggregationField> aggregationFields = getRequiredFields(fields);
	MatchOperation matchOperation = match(matchCriteria);
	ProjectionOperation projectionOperation = reduce(project(), aggregationFields, AggregationField::projectField);
	GroupOperation groupOperation = reduce(group(), aggregationFields, AggregationField::groupField);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			projectionOperation,
			groupOperation,
			addFields().withAggregationFields("result", aggregationFields).build(),
			projectFields("result").build()
		)
		.build();
	
	RollupResultDTO resultDTO =  executeSingleAggregation(aggregation, RollupResultDTO.class);
	
	return resultDTO != null ? resultDTO : new RollupResultDTO(getDefaults(aggregationFields));
    }
    
    public RollupResultDTO averageCallFieldsByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallField> fields) {
	Criteria matchCriteria = getCompanyConstrained(companyId).andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);
	
	return averageCallFields(matchCriteria, fields);
    }
    
    public RollupResultDTO averageCallFieldsByUser(String userId, DateTime startDate, DateTime endDate, List<CallField> fields) {
	Criteria matchCriteria = getUserConstrained(userId).andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);
	
	return averageCallFields(matchCriteria, fields);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria matchCriteria, List<CallField> fields) {
	return rollupCallField(matchCriteria, RollupCadence.DAILY, fields);
    }
    
    public List<RollupResultDTO> rollupCallField(Criteria matchCriteria, RollupCadence cadence, List<CallField> fields) {
	List<AggregationField> aggregationFields = getRequiredFields(fields);
	MatchOperation matchOperation = match(matchCriteria);
	ProjectionOperation projectionOperation = reduce(project(), aggregationFields, AggregationField::projectField);
	GroupOperation groupOperation = reduce(group("rollup"), aggregationFields, AggregationField::groupField);
	
	TypedAggregation<Call> aggregation = anAggregation()
		.append(
			matchOperation,
			projectionOperation
				.and("createDate").dateAsFormattedString(cadence.getValue()).as("rollup"),
			groupOperation,
			addFields().withAggregationFields("result", aggregationFields).build(),
			projectFields("result").and(TIMESTAMP).previousOperation().build(),
			sortFields(Direction.ASC, TIMESTAMP).build()
		)
		.build();
	
	return executeAggregation(aggregation, RollupResultDTO.class);
    }
    
    public List<RollupResultDTO> rollupCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallField> fields) {
	Criteria matchCriteria = getCompanyConstrained(companyId).andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);
	
	return rollupCallField(matchCriteria, cadence, fields);
    }
    
    public List<RollupResultDTO> rollupCallFieldByUser(String userId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallField> fields) {
	Criteria matchCriteria = getUserConstrained(userId).andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);
	
	return rollupCallField(matchCriteria, cadence, fields);
    }
}

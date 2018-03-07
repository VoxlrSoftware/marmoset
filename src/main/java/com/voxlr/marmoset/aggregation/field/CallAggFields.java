package com.voxlr.marmoset.aggregation.field;

import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.groupCountModifier;
import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.groupSumModifier;
import static com.voxlr.marmoset.util.MapUtils.mapOf;
import static com.voxlr.marmoset.util.MapUtils.KVPair.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.voxlr.marmoset.model.ConvertibleEnum;

public class CallAggFields {
    public static AggregationField field(CallField field) {
	return callAggregationFields.get(field);
    }
    
    public static enum CallField implements ConvertibleEnum {
	CREATE_DATE("createDate"),
	CALL_OUTCOME("callOutcome"),
	COMPANY_ID("companyId"),
	USER_ID("userId"),
	CALL_STRATEGY_NAME("callStrategyName"),
	TOTAL_TALK_TIME("totalTalkTime"),
	DURATION("duration"),
	DETECTED_PHRASE_COUNT("detectedPhraseCount"),
	DETECTION_RATIO("detectionRatio"),
	CUSTOMER_TALK_RATIO("customerTalkRatio"),
	CONVERSATION("conversation"),
	TOTAL_COUNT("totalCount")
	;
	
	static Map<String, CallField> callFields;
	
	static {
	    callFields = Arrays.asList(CallField.values()).stream().collect(Collectors.toMap(CallField::get, Function.identity()));
	}
	
	public static List<CallField> getAll() {
	    return new ArrayList<CallField>(callFields.values());
	}

	@JsonCreator
	public static CallField fromString(String value) {
	    return callFields.get(value);
	}
	
	private String name;
	
	private CallField(String name) {
	    this.name = name;
	}
	
	@JsonValue
	public String get() {
	    return this.name;
	}
    }
    
    private static final Map<CallField, AggregationField> callAggregationFields = mapOf(
	entry(
		CallField.CREATE_DATE,
		AggregationField.builder(CallField.CREATE_DATE.get())
		.ableToRollup(false)
		.build()
	),
	entry(
		CallField.CALL_OUTCOME,
		AggregationField.builder(CallField.CALL_OUTCOME.get())
		.ableToRollup(false)
		.build()
	),
	entry(
		CallField.COMPANY_ID,
		AggregationField.builder(CallField.COMPANY_ID.get())
		.ableToRollup(false)
		.build()
	),
	entry(
		CallField.USER_ID,
		AggregationField.builder(CallField.USER_ID.get())
		.ableToRollup(false)
		.build()
	),
	entry(
		CallField.CALL_STRATEGY_NAME,
		AggregationField.builder(CallField.CALL_STRATEGY_NAME.get())
		.pathName("callStrategy.name")
		.ableToRollup(false)
		.build()
	),
	entry(
		CallField.TOTAL_TALK_TIME,
		AggregationField.builder(CallField.TOTAL_TALK_TIME.get())
		.pathName("statistics.totalTalkTime")
		.defaultValue(0)
		.build()
	),
	entry(
		CallField.DURATION,
		AggregationField.builder(CallField.DURATION.get())
		.pathName("statistics.duration")
		.defaultValue(0)
		.build()
	),
	entry(
		CallField.DETECTED_PHRASE_COUNT,
		AggregationField.builder(CallField.DETECTED_PHRASE_COUNT.get())
		.pathName("analysis.detectedPhraseCount")
		.defaultValue(0)
		.build()
	),
	entry(
		CallField.DETECTION_RATIO,
		AggregationField.builder(CallField.DETECTION_RATIO.get())
		.pathName("analysis.detectionRatio")
		.defaultValue(0.0)
		.build()
	),
	entry(
		CallField.CUSTOMER_TALK_RATIO,
		AggregationField.builder(CallField.CUSTOMER_TALK_RATIO.get())
		.projectOperationModifier((project, field) ->
			project.andExpression("cond(statistics.totalTalkTime > 0, statistics.customerTalkTime / statistics.totalTalkTime, 0)")
		)
		.defaultValue(0.0)
		.build()
	),
	entry(
		CallField.CONVERSATION,
		AggregationField.builder(CallField.CONVERSATION.get())
		.projectOperationModifier((project, field) ->
			project.andExpression("cond(in(callOutcome, new String[]{'Lost', 'Won', 'Progress'}), 1, 0)")
		)
		.groupOperationModifier(groupSumModifier)
		.defaultValue(0)
		.build()
	),
	entry(
		CallField.TOTAL_COUNT,
		AggregationField.builder(CallField.TOTAL_COUNT.get())
		.pathName("analysis.detectionRatio")
		.projectable(false)
		.groupOperationModifier(groupCountModifier)
		.defaultValue(0)
		.build()
	));
}

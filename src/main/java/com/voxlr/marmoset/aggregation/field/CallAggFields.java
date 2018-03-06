package com.voxlr.marmoset.aggregation.field;

import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.groupCountModifier;
import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.groupSumModifier;
import static com.voxlr.marmoset.util.MapUtils.mapOf;
import static com.voxlr.marmoset.util.MapUtils.KVPair.entry;

import java.util.Map;

public class CallAggFields {
    public static String CREATE_DATE = "createDate";
    public static String CALL_OUTCOME = "callOutcome";
    public static String COMPANY_ID = "companyId";
    public static String USER_ID = "userId";
    public static String CALL_STRATEGY_NAME = "callStrategyName";
    public static String TOTAL_TALK_TIME = "totalTalkTime";
    public static String DURATION = "duration";
    public static String DETECTED_PHRASE_COUNT = "detectedPhraseCount";
    public static String DETECTION_RATIO = "detectionRatio";
    public static String CUSTOMER_TALK_RATIO = "customerTalkRatio";
    public static String CONVERSATION = "conversation";
    public static String TOTAL_COUNT = "totalCount";
    
    public final Map<String, AggregationField> callAggregationFields = mapOf(
	entry(
		CREATE_DATE,
		AggregationField.builder(CREATE_DATE)
		.ableToRollup(false)
		.build()
	),
	entry(
		CALL_OUTCOME,
		AggregationField.builder(CALL_OUTCOME)
		.ableToRollup(false)
		.build()
	),
	entry(
		COMPANY_ID,
		AggregationField.builder(COMPANY_ID)
		.ableToRollup(false)
		.build()
	),
	entry(
		COMPANY_ID,
		AggregationField.builder(COMPANY_ID)
		.ableToRollup(false)
		.build()
	),
	entry(
		USER_ID,
		AggregationField.builder(USER_ID)
		.ableToRollup(false)
		.build()
	),
	entry(
		CALL_STRATEGY_NAME,
		AggregationField.builder(CALL_STRATEGY_NAME)
		.pathName("callStrategy.name")
		.ableToRollup(false)
		.build()
	),
	entry(
		TOTAL_TALK_TIME,
		AggregationField.builder(TOTAL_TALK_TIME)
		.pathName("statistics.totalTalkTime")
		.defaultValue(0)
		.build()
	),
	entry(
		DURATION,
		AggregationField.builder(DURATION)
		.pathName("statistics.duration")
		.defaultValue(0)
		.build()
	),
	entry(
		DETECTED_PHRASE_COUNT,
		AggregationField.builder(DETECTED_PHRASE_COUNT)
		.pathName("analysis.detectedPhraseCount")
		.defaultValue(0)
		.build()
	),
	entry(
		DETECTION_RATIO,
		AggregationField.builder(DETECTION_RATIO)
		.pathName("analysis.detectionRatio")
		.defaultValue(0)
		.build()
	),
	entry(
		CUSTOMER_TALK_RATIO,
		AggregationField.builder(CUSTOMER_TALK_RATIO)
		.projectOperationModifier((project, field) ->
			project.andExpression("cond(statistics.totalTalkTime > 0, statistics.customerTalkTime / statistics.totalTalkTime, 0)")
		)
		.defaultValue(0.0)
		.build()
	),
	entry(
		CONVERSATION,
		AggregationField.builder(CONVERSATION)
		.projectOperationModifier((project, field) ->
			project.andExpression("cond(in(callOutcome, new String[]{'Lost', 'Won', 'Progress'}), 1, 0)")
		)
		.groupOperationModifier(groupSumModifier)
		.defaultValue(0)
		.build()
	),
	entry(
		TOTAL_COUNT,
		AggregationField.builder(TOTAL_COUNT)
		.pathName("analysis.detectionRatio")
		.projectable(false)
		.groupOperationModifier(groupCountModifier)
		.defaultValue(0)
		.build()
	));
}

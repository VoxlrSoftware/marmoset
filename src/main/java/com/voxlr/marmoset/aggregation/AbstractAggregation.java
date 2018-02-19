package com.voxlr.marmoset.aggregation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import com.voxlr.marmoset.model.dto.aggregation.TotalCountDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAggregation {
    
    private final MongoTemplate mongoTemplate;
    
    public MongoTemplate mongo() {
	return mongoTemplate;
    }

    public <T> int doCount(Class<T> entityClass, MatchOperation matchOperation) {
	TypedAggregation<T> countAggregation = newAggregation(
		entityClass,
		matchOperation,
		group().count().as("totalCount"),
		project().and("totalCount").previousOperation());
	
	AggregationResults<TotalCountDTO> totalCountResults = mongoTemplate.aggregate(countAggregation, TotalCountDTO.class);
	List<TotalCountDTO> results = totalCountResults.getMappedResults();
	
	return results.size() > 0 ? results.get(0).getTotalCount() : 0;
    }
}

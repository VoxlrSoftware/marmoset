package com.voxlr.marmoset.aggregation;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import com.voxlr.marmoset.model.dto.aggregation.TotalCountDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAggregation<T> {
    
    private final MongoTemplate mongoTemplate;
    private final Class<T> entityClass;
    
    public MongoTemplate mongo() {
	return mongoTemplate;
    }

    public int doCount(MatchOperation matchOperation) {
	TypedAggregation<T> countAggregation = newAggregation(
		entityClass,
		matchOperation,
		group().count().as("totalCount"),
		project("totalCount"));
	
	AggregationResults<TotalCountDTO> totalCountResults = mongoTemplate.aggregate(countAggregation, TotalCountDTO.class);
	List<TotalCountDTO> results = totalCountResults.getMappedResults();
	
	return results.size() > 0 ? results.get(0).getTotalCount() : 0;
    }
    
    public <U> Page<U> executePagedAggregation(MatchOperation matchOperation, Pageable pageable, TypedAggregation<T> aggregation, Class<U> outputClass) {
	int totalCount = doCount(matchOperation);
	if (totalCount == 0) {
	    return new PageImpl<>(newArrayList(), pageable, totalCount);
	}
	
	AggregationResults<U> results = mongoTemplate.aggregate(aggregation, outputClass);
	return new PageImpl<>(results.getMappedResults(), pageable, totalCount);
    }
    
    public CustomAggregation<T> anAggregation() {
	    return new CustomAggregation<>(entityClass);
	}
    
    public static class CustomAggregation<T> {
	private List<AggregationOperation> operationList;
	private final Class<T> entityClass;
	
	private CustomAggregation(Class<T> entityClass) {
	    this.entityClass = entityClass;
	    operationList = newArrayList();
	}
	
	public TypedAggregation<T> build() {
	    return newAggregation(
		    entityClass,
		    operationList);
	}
	
	public CustomAggregation<T> append(AggregationOperation... operations) {
	    operationList.addAll(listOf(operations));
	    return this;
	}
	
	public CustomAggregation<T> withPaging(Pageable pageable, SortOperation defaultSort) {
	    SortOperation sortOperation = null;
	    if (pageable.getSort().isSorted()) {
		sortOperation = sort(pageable.getSort());
	    } else {
		sortOperation = sort(Direction.DESC, "createDate");
	    }
	    
	    operationList.addAll(listOf(
		    sortOperation,
		    skip((long) pageable.getPageNumber() * pageable.getPageSize()),
		    limit(pageable.getPageSize())
		));
	    return this;
	}
    }
}

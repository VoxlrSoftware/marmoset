package com.voxlr.marmoset.aggregation.field;

import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation.GroupOperationBuilder;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation.ProjectionOperationBuilder;

public class AggregationOperationModifiers {
    @FunctionalInterface
    public interface ProjectOperationModifier {
	ProjectionOperationBuilder project(ProjectionOperation input, AggregationField field);
    }
    
    @FunctionalInterface
    public interface GroupOperationModifier {
	GroupOperationBuilder group(GroupOperation input, AggregationField field);
    }
    
    public final static GroupOperationModifier groupAverageModifier = (group, field) -> {
	return group.avg(field.getPathName());
    };
    
    public final static GroupOperationModifier groupSumModifier = (group, field) -> {
	return group.sum(field.getPathName());
    };
    
    public final static GroupOperationModifier groupCountModifier = (group, field) -> {
	return group.count();
    };
    
    public final static ProjectOperationModifier projectFieldModifier = (project, field) -> {
	return project.and(field.getPathName());
    };
}

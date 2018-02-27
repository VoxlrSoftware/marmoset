package com.voxlr.marmoset.aggregation.operation;

import static com.voxlr.marmoset.util.ListUtils.listOf;

import java.util.List;

import org.bson.Document;

public class ProjectFieldsOperation extends CustomOperation {
    
    private List<Object> fields;
    
    private ProjectFieldsOperation(Object... fields) {
	this.fields = listOf(fields);
    }
    
    public static ProjectFieldsOperation projectFields(Object... fields) {
	return new ProjectFieldsOperation(fields);
    }
    
    public ProjectExp and(String projectName) {
	ProjectExp exp = new ProjectExp(this, projectName);
	fields.add(exp);
	return exp;
    }

    @Override
    protected void doBuild(Document customOperation) {
	Document projectOperation = new Document();
	fields.stream().forEach(field -> {
	    if (field instanceof String) {
		projectOperation.append((String)field, internal((String)field));
	    } else if (field instanceof ProjectExp) {
		((ProjectExp) field).doBuild(projectOperation);
	    }
	});
	customOperation.append("$project", projectOperation);
    }
    
    public class ProjectExp {
	private String projectName;
	private String expression;
	private ProjectFieldsOperation operation;
	
	public ProjectExp(ProjectFieldsOperation operation, String projectName) {
	    this.operation = operation;
	    this.projectName = projectName;
	}
	
	public ProjectFieldsOperation previousOperation() {
	    this.expression = "$_id";
	    return operation;
	}
	
	public ProjectFieldsOperation withExpression(String expression) {
	    this.expression = expression;
	    return operation;
	}
	
	public ProjectFieldsOperation withExpression(Document expression) {
	    this.expression = expression.toJson();
	    return operation;
	}
	
	public void doBuild(Document document) {
	    document.append(projectName, expression);
	}
    }
}

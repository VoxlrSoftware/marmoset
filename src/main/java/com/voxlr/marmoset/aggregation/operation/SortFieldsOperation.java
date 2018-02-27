package com.voxlr.marmoset.aggregation.operation;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort.Direction;

import lombok.AllArgsConstructor;

public class SortFieldsOperation extends CustomOperation {
    
    private List<SortOp> operations = newArrayList();
    
    private SortFieldsOperation(Direction direction, String field) {
	this.operations.add(new SortOp(direction, field));
    }
    
    public SortFieldsOperation and(Direction direction, String field) {
	this.operations.add(new SortOp(direction, field));
	return this;
    }
    
    public static SortFieldsOperation sortFields(Direction direction, String field) {
	return new SortFieldsOperation(direction, field);
    }

    @Override
    protected void doBuild(Document customOperation) {
	Document sortDoc = new Document();
	operations.stream().forEach(sortOp -> sortOp.doBuild(sortDoc));
	customOperation.append("$sort", sortDoc);
    }
    
    @AllArgsConstructor
    public class SortOp extends CustomOperation {
	private Direction direction;
	private String field;
	
	private int getDirectionValue() {
	    if (direction == Direction.DESC) {
		return -1;
	    }
	    
	    return 1;
	}
	
	@Override
	protected void doBuild(Document customOperation) {
	    customOperation.append(field, getDirectionValue());
	}
	
    }

}

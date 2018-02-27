package com.voxlr.marmoset.aggregation.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;

public class AddFieldsOperation extends CustomOperation {
    private Map<String, BasicDBObject> setList;
    
    private AddFieldsOperation() {
	setList = new HashMap<>();
    }
    
    public static AddFieldsOperation addFields() {
	return new AddFieldsOperation();
    }
    
    public AddFieldsOperation withObject(String objectName, List<String> fields) {
	BasicDBObject set = new BasicDBObject();
	fields.stream().forEach(field -> set.append(field, internal(field)));
	
	setList.put(objectName, set);
	return this;
    }

    @Override
    protected void doBuild(Document customOperation) {
	Document addFields = new Document();
	setList.keySet().stream().forEach(key -> {
	    addFields.append(key, setList.get(key));
	});
	
	customOperation.append("$addFields", addFields);
    }
}

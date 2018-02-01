package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallStrategy extends AuditModel {
    private String name;
    
    private List<String> phrases = new ArrayList<String>();
    
    public CallStrategy update(String name, List<String> phrases) {
	if (name != null) {
	    this.name = name;
	}
	
	if (phrases != null) {
	    this.phrases = phrases;
	}
	
	this.setLastModified(new Date());
	return this;
    }

    public static CallStrategy createNew() {
	CallStrategy callStrategy = new CallStrategy();
	callStrategy.setId(new ObjectId().toHexString());
	callStrategy.setCreateDate(new Date());
	return callStrategy;
    }
}

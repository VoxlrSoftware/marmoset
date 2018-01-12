package com.voxlr.marmoset.model.persistence.dto;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.persistence.CallAnalysis;
import com.voxlr.marmoset.model.persistence.CallStatistic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallDTO {
    private String companyId;
    private String userId;
    private String callSid;
    private PhoneNumberHolder employeeNumber;
    private PhoneNumberHolder customerNumber;
    private String recordingUrl;
    private BasicDBObject externalReferences;
    private List<String> strategyList;
    private CallStatistic statistics;
    private CallAnalysis analysis;
}

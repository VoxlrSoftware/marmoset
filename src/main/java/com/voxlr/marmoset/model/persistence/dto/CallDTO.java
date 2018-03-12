package com.voxlr.marmoset.model.persistence.dto;

import com.mongodb.BasicDBObject;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallAnalysis;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
  private Call.Statistic statistics;
  private CallAnalysis analysis;
}

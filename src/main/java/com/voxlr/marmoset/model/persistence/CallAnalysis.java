package com.voxlr.marmoset.model.persistence;

import com.voxlr.marmoset.model.CallScopedEntity;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Document(collection = "callAnalysis")
@Getter
@Setter
@NoArgsConstructor
public class CallAnalysis extends Entity implements CallScopedEntity {
  private ObjectId callId;
  private String companyId;
  private String userId;
  private PhoneNumberHolder employeeNumber;
  private PhoneNumberHolder customerNumber;
  private String callOutcome;
  private String callStrategyName;
  private int duration = 0;
  private int totalTalkTime = 0;
  private int customerTalkTime = 0;
  private int employeeTalkTime = 0;
  private double detectionRatio = 0;
  private int detectedPhraseCount = 0;
  private Date createDate;
}

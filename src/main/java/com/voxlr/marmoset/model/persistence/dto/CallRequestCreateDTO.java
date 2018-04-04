package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;

@Getter
@Setter
public class CallRequestCreateDTO {
  @PhoneNumberValidConstraint(required = true)
  private PhoneNumberHolder customerNumber;

  @PhoneNumberValidConstraint(required = true)
  private PhoneNumberHolder callerId;

  @NotNull private ObjectId strategyId;
}

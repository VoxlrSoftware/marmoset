package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;
import javax.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallCreateDTO {
  @NotBlank
  private String callSid;

  @PhoneNumberValidConstraint private PhoneNumberHolder employeeNumber;

  @PhoneNumberValidConstraint private PhoneNumberHolder customerNumber;

  @Singular("strategy")
  private List<String> strategyList;
}

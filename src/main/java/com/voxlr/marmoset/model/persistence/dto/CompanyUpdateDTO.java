package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {

  private String id;
  private String name;

  @PhoneNumberValidConstraint private PhoneNumberHolder phoneNumber;

  @Singular private List<CallStrategyDTO> callStrategies;
}

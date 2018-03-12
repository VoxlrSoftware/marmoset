package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.service.domain.ValidationRequestService.ValidationType;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidatePhoneRequestDTO {

  @PhoneNumberValidConstraint(required = true)
  private PhoneNumberHolder phoneNumber;

  private ValidationType type;

  private String entityId;
}

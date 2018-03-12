package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@Builder
public class CompanyCreateDTO {

  @NotBlank private String name;

  @PhoneNumberValidConstraint private PhoneNumberHolder phoneNumber;
}

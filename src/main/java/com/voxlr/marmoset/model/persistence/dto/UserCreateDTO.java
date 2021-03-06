package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.CompanyExistsConstraint;
import com.voxlr.marmoset.validation.constraint.TeamExistsConstraint;
import com.voxlr.marmoset.validation.constraint.UniqueEmailConstraint;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDTO {

  @CompanyExistsConstraint private String companyId;

  @TeamExistsConstraint private String teamId;

  @NotBlank private String firstName;

  @NotBlank private String lastName;

  @NotBlank @Email @UniqueEmailConstraint private String email;

  private String role;
}

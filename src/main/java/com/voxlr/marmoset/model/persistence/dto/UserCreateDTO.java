package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.CompanyExistsConstraint;
import com.voxlr.marmoset.validation.constraint.TeamExistsConstraint;
import com.voxlr.marmoset.validation.constraint.UniqueEmailConstraint;
import javax.validation.constraints.Email;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDTO {

  @CompanyExistsConstraint private ObjectId companyId;

  @TeamExistsConstraint private ObjectId teamId;

  private String firstName;

  private String lastName;

  @Email
  @UniqueEmailConstraint private String email;

  private String role;
}

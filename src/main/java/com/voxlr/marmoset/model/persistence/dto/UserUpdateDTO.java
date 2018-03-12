package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.TeamExistsConstraint;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

  @TeamExistsConstraint private String teamId;
  private String firstName;
  private String lastName;
  private String password;
  private String role;
  private String id;
}

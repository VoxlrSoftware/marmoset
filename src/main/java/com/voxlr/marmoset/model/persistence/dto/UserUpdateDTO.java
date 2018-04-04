package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.TeamExistsConstraint;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

  @TeamExistsConstraint private ObjectId teamId;
  private String firstName;
  private String lastName;
  private String password;
  private String role;
  private ObjectId id;
}

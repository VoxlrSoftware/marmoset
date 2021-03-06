package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.CompanyExistsConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@NoArgsConstructor
@Builder
@Getter
@Setter
public class TeamCreateDTO {
  @NotBlank private String name;

  @CompanyExistsConstraint private String companyId;

  public TeamCreateDTO(String name, String companyId) {
    this.name = name;
    this.companyId = companyId;
  }
}

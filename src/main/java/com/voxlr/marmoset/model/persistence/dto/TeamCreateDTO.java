package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.CompanyExistsConstraint;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Builder
@Getter
@Setter
public class TeamCreateDTO {
  @NotBlank
  private String name;

  @CompanyExistsConstraint private String companyId;

  public TeamCreateDTO(String name, String companyId) {
    this.name = name;
    this.companyId = companyId;
  }
}

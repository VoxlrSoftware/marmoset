package com.voxlr.marmoset.model.persistence.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallStrategyDTO {
  private String id;

  @NotNull private String name;

  @NotNull private List<String> phrases;
}

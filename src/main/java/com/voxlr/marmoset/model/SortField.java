package com.voxlr.marmoset.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SortField {
  private String sortBy;
  private String sortOrder;
}

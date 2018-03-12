package com.voxlr.marmoset.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
  private int page;
  private int pageSize;

  @Builder.Default private List<SortField> sortFields = new ArrayList<SortField>();
}

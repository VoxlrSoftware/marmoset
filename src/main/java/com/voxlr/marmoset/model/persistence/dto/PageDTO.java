package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.Pagination;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO<T> {
  private List<T> results;
  private long totalCount;
  private int totalPages;
  private Pagination pagination;
}

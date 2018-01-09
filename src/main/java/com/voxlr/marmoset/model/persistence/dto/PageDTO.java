package com.voxlr.marmoset.model.persistence.dto;

import java.util.List;

import com.voxlr.marmoset.model.Pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

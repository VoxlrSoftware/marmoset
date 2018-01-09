package com.voxlr.marmoset.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private int page;
    private int pageSize;
    
    @Builder.Default
    private List<SortField> sortFields = new ArrayList<SortField>();
}

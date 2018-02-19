package com.voxlr.marmoset.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;

public interface CustomizedCallRepository {
    Page<CallAggregateDTO> aggregateCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable);
}

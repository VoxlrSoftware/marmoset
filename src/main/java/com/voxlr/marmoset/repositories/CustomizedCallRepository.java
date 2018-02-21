package com.voxlr.marmoset.repositories;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;

public interface CustomizedCallRepository {
    Page<CallAggregateDTO> getCallsByCompany(String companyId, DateTime startDate, DateTime endDate, Pageable pageable);
    Page<CallAggregateDTO> getCallsByUser(String userId, DateTime startDate, DateTime endDate, Pageable pageable);
    RollupResultDTO averageCallFieldByUser(String userId, DateTime startDate, DateTime endDate, CallAggregationField field);
    RollupResultDTO averageCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate, CallAggregationField field);
}

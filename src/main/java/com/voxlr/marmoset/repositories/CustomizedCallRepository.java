package com.voxlr.marmoset.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voxlr.marmoset.aggregation.CallAggregation.CallAggregationField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;

public interface CustomizedCallRepository {
    Page<CallAggregateDTO> getCallsByCompany(String companyId, Date startDate, Date endDate, Pageable pageable);
    Page<CallAggregateDTO> getCallsByUser(String userId, Date startDate, Date endDate, Pageable pageable);
    RollupResultDTO averageCallFieldByUser(String userId, Date startDate, Date endDate, CallAggregationField field);
    RollupResultDTO averageCallFieldByCompany(String companyId, Date startDate, Date endDate, CallAggregationField field);
}

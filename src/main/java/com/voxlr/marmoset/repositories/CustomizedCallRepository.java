package com.voxlr.marmoset.repositories;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.field.CallAggFields.CallField;
import com.voxlr.marmoset.model.dto.aggregation.CallAggregateDTO;
import com.voxlr.marmoset.model.dto.aggregation.RollupResultDTO;

public interface CustomizedCallRepository {
    Page<CallAggregateDTO> getCallsByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable);
    Page<CallAggregateDTO> getCallsByUser(String userId, DateTime startDate, DateTime endDate, List<CallField> fields, Pageable pageable);
    RollupResultDTO averageCallFieldByUser(String userId, DateTime startDate, DateTime endDate, List<CallField> fields);
    RollupResultDTO averageCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate, List<CallField> fields);
    List<RollupResultDTO> rollupCallFieldByUser(String userId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallField> fields);
    List<RollupResultDTO> rollupCallFieldByCompany(String companyId, DateTime startDate, DateTime endDate, RollupCadence cadence, List<CallField> fields);
}

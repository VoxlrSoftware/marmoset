package com.voxlr.marmoset.repositories;

import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.persistence.CallAnalysis;

public interface CallAnalysisRepository extends CrudRepository<CallAnalysis, String> {
}

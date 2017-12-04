package com.voxlr.marmoset.repositories;

import org.springframework.data.repository.CrudRepository;

import com.voxlr.marmoset.model.Company;

public interface CompanyRepository extends CrudRepository<Company, String> {
    @Override
    Company findOne(String id);
    
    @Override
    void delete(Company deleted);
}

package com.voxlr.marmoset.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.Company;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.util.error.ApiError;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    
    @Autowired
    CompanyRepository companyRepository;
    
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<Iterable<Company>> company() {
	Iterable<Company> companies = companyRepository.findAll();
	return new ResponseEntity<Iterable<Company>>(companies, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="{id}")
    public ResponseEntity<?> getCompany(@PathVariable String id) throws EntityNotFoundException {
	Company company = companyRepository.findOne(id);
	if (company == null) {
	    throw new EntityNotFoundException(Company.class, "id", id);
	}
	
	return new ResponseEntity<Company>(company, HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String save(@RequestBody Company company) {
	companyRepository.save(company);
	return company.getId();
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="{id}")
    public Company update(@PathVariable String id, @RequestBody Company companyUpdate) {
	Company company = companyRepository.findOne(id);
	if (companyUpdate.getName() != null) {
	    company.setName(company.getName());
	}
	
	companyRepository.save(company);
	return company;
    }
    
    @RequestMapping(method=RequestMethod.DELETE, value="{id}")
    public String delete(@PathVariable String id) {
	Company company = companyRepository.findOne(id);
	companyRepository.delete(company);
	
	return "company deleted";
    }
}

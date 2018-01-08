package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.model.persistence.dto.TeamCreateDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@Service
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private TeamService teamService;
    
    public boolean validateExists(String id) {
	return companyRepository.findIdById(id) != null;
    }
    
    public Company get(String id, AuthUser authUser) throws EntityNotFoundException {
	Company company = companyRepository.findOne(id);
	
	if (company == null) {
	    throw new EntityNotFoundException(Company.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to view user");
	}
	
	return company;
    }
    
    public Company create(CompanyCreateDTO companyCreateDTO, AuthUser authUser) {
	if (!authorizationService.canCreate(authUser, Company.class)) {
	    throw new UnauthorizedUserException("Account unauthorized to create company");
	}
	
	Company company = modelMapper.map(companyCreateDTO, Company.class);
	
	company = companyRepository.save(company);

	TeamCreateDTO defaultTeam = TeamCreateDTO.builder().name("Default").build();
	teamService.createTeamInternal(company, defaultTeam);

	company = companyRepository.save(company);
	
	return company;
    }
    
    public Company update(CompanyUpdateDTO companyUpdateDTO, AuthUser authUser) throws EntityNotFoundException {	
	Company company = companyRepository.findOne(companyUpdateDTO.getId());
	
	if (company == null) {
	    throw new EntityNotFoundException(Company.class, "id", companyUpdateDTO.getId());
	}
	
	if (!authorizationService.canWrite(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to update company");
	}
	
	if (companyUpdateDTO.getName() != null) {
	    company.setName(companyUpdateDTO.getName());
	}
	
	if (companyUpdateDTO.getPhoneNumber() != null) {
	    company.setPhoneNumber(companyUpdateDTO.getPhoneNumber());
	}
	
	company = companyRepository.save(company);
	return company;
    }
    
    public void delete(String id, AuthUser authUser) throws EntityNotFoundException {
	Company company = companyRepository.findOne(id);
	
	if (company == null) {
	    throw new EntityNotFoundException(Company.class, "id", id);
	}
	
	if (!authorizationService.canWrite(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete company");
	}
	
	companyRepository.delete(company);
    }
}

package com.voxlr.marmoset.service.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.model.persistence.dto.TeamCreateDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.service.AuthorizationService;

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
    
    public Company getInternalAndThrow(String companyId) throws EntityNotFoundException {
	Optional<Company> result = companyRepository.findById(companyId);
	
	if (!result.isPresent()) {
	    throw new EntityNotFoundException(Company.class, "id", companyId);
	}
	
	return result.get();
    }
    
    public CallStrategy findCallStrategy(String companyId, String strategyId) throws EntityNotFoundException {
	Company company = getInternalAndThrow(companyId);
	
	Optional<CallStrategy> strategy = company.getCallStrategies().stream()
		.filter(x -> x.getId().equals(strategyId)).findFirst();
	
	if (!strategy.isPresent()) {
	    throw new EntityNotFoundException(CallStrategy.class, "id", strategyId);
	}
	
	return strategy.get();
    }
    
    public Company get(String id, AuthUser authUser) throws EntityNotFoundException {
	Company company = getInternalAndThrow(id);
	
	if (!authorizationService.canRead(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to view company");
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
	Company company = getInternalAndThrow(companyUpdateDTO.getId());
	
	if (!authorizationService.canWrite(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to update company");
	}
	
	if (companyUpdateDTO.getName() != null) {
	    company.setName(companyUpdateDTO.getName());
	}
	
	if (companyUpdateDTO.getPhoneNumber() != null) {
	    company.setPhoneNumber(companyUpdateDTO.getPhoneNumber());
	}
	
	if (companyUpdateDTO.getCallStrategies() != null) {
	    List<CallStrategy> newStrategies = newArrayList();
	    List<CallStrategy> currentStrategies = company.getCallStrategies();
	    
	    companyUpdateDTO.getCallStrategies().stream().forEach(strategyDTO -> {
		CallStrategy strategy = null;
		
		if (strategyDTO.getId() != null) {
		    Optional<CallStrategy> currentStrategy = 
			    currentStrategies.stream().filter(x -> x.getId().equals(strategyDTO.getId())).findFirst();
		    if (currentStrategy.isPresent()) {
			strategy = currentStrategy.get();
		    }
		}
		
		if (strategy == null) {
		    strategy = CallStrategy.createNew();
		}
		
		strategy.update(strategyDTO.getName(), strategyDTO.getPhrases());
		newStrategies.add(strategy);
	    });
	    
	    company.setCallStrategies(newStrategies);
	}
	
	company = companyRepository.save(company);
	return company;
    }
    
    public void delete(String id, AuthUser authUser) throws EntityNotFoundException {
	Company company = getInternalAndThrow(id);
	
	if (!authorizationService.canWrite(authUser, company)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete company");
	}
	
	companyRepository.delete(company);
    }
}

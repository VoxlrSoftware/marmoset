package com.voxlr.marmoset.controller;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.RemovedEntityDTO;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.dto.CompanyCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyDTO;
import com.voxlr.marmoset.model.persistence.dto.CompanyUpdateDTO;
import com.voxlr.marmoset.repositories.CompanyRepository;
import com.voxlr.marmoset.service.domain.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class CompanyController extends ApiController {
  public static final String COMPANY = "/company";

  @Autowired CompanyRepository companyRepository;

  @Autowired CompanyService companyService;

  @Autowired private ModelMapper modelMapper;

  @RequestMapping(method = RequestMethod.GET, value = COMPANY + "/{id}")
  public ResponseEntity<?> getCompany(
      @PathVariable String id, @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    Company company = companyService.get(id, authUser);
    CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);

    return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST, value = COMPANY)
  public ResponseEntity<?> createCompany(
      @Valid @RequestBody CompanyCreateDTO companyCreateDTO,
      @AuthenticationPrincipal AuthUser authUser) {
    Company company = companyService.create(companyCreateDTO, authUser);
    CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);

    return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, value = COMPANY + "/{id}")
  public ResponseEntity<?> updateCompany(
      @PathVariable String id,
      @Valid @RequestBody CompanyUpdateDTO companyUpdateDTO,
      @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    companyUpdateDTO.setId(id);
    Company company = companyService.update(companyUpdateDTO, authUser);
    CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);

    return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = COMPANY + "/{id}")
  public ResponseEntity<?> deleteCompany(
      @PathVariable String id, @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    companyService.delete(id, authUser);

    RemovedEntityDTO removedEntityDTO = new RemovedEntityDTO(id);

    return new ResponseEntity<RemovedEntityDTO>(removedEntityDTO, HttpStatus.OK);
  }
}

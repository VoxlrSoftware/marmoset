package com.voxlr.marmoset.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.Phoneable;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.ValidationRequest;
import com.voxlr.marmoset.model.persistence.dto.ValidatePhoneRequestDTO;
import com.voxlr.marmoset.model.persistence.dto.ValidatePhoneResponseDTO;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.service.ValidationRequestService;
import com.voxlr.marmoset.service.ValidationRequestService.ValidationType;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@RestController
public class ValidationController extends ApiController {
    public static final String VALIDATE = "/validate";
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private ValidationRequestService validationRequestService;
    
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ModelMapper modelMapper;
    
    @SuppressWarnings("serial")
    private HashMap<ValidationType, Class<? extends Phoneable>> typeMap = new HashMap<ValidationType, Class<? extends Phoneable>>() {{
	put(ValidationType.COMPANY, Company.class);
	put(ValidationType.USER, User.class);
    }};
    
    @RequestMapping(
	    value = VALIDATE,
	    method = RequestMethod.POST)
    public ResponseEntity<?> validateNumber(
	    @Valid @RequestBody ValidatePhoneRequestDTO validatePhoneRequestDTO,
	    @AuthenticationPrincipal AuthUser authUser) throws Exception {
	
	if (!typeMap.containsKey(validatePhoneRequestDTO.getType())) {
	    throw new Exception("Bad data");
	}
	
	Class<? extends Phoneable> typeClass = typeMap.get(validatePhoneRequestDTO.getType());
	
	Phoneable entity = mongoTemplate.findById(validatePhoneRequestDTO.getEntityId(), typeClass);
	if (entity == null) {
	    throw new EntityNotFoundException(typeClass, "id", validatePhoneRequestDTO.getEntityId());
	}
	
	if (!authorizationService.canWrite(authUser, entity)) {
	    throw new UnauthorizedUserException("User unauthorized to modify " + 
		    typeClass.getSimpleName() + " with id [" + entity.getId() + "]");
	}
	
	ValidatePhoneResponseDTO responseDTO = 
		validationRequestService.validatePhoneNumber(
			authUser, entity, validatePhoneRequestDTO.getPhoneNumber());
	
	return new ResponseEntity<ValidatePhoneResponseDTO>(responseDTO, HttpStatus.OK);
    }
    
    @RequestMapping(
	    value = VALIDATE + "/{id}",
	    method = RequestMethod.GET)
    public ResponseEntity<?> checkValidation(
	    @PathVariable String id,
	    @AuthenticationPrincipal AuthUser authUser) {
	ValidationRequest request = validationRequestService.getRequest(id, authUser);
	ValidatePhoneResponseDTO responseDTO = modelMapper.map(request, ValidatePhoneResponseDTO.class);
	return new ResponseEntity<ValidatePhoneResponseDTO>(responseDTO, HttpStatus.OK);
    }
}
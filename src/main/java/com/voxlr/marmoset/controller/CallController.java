package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.controller.CompanyController.COMPANY;
import static com.voxlr.marmoset.controller.UserController.USER;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallRequest;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.util.MapperUtils;
import javax.validation.Valid;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CallController extends ApiController {
  public static final String CALL = "/call";
  public static final String CALL_REQUEST = CALL + "/request";
  public static final String COMPANY_CALL = COMPANY + "/{companyId}" + CALL;
  public static final String USER_CALL = USER + "/{userId}" + CALL;

  @Autowired private CallService callService;

  @Autowired private ModelMapper modelMapper;

  @Autowired private MapperUtils mapperUtils;

  @RequestMapping(method = RequestMethod.GET, value = CALL + "/{id}")
  public ResponseEntity<?> get(@PathVariable ObjectId id, @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    Call call = callService.get(id, authUser);
    CallDTO callDTO = modelMapper.map(call, CallDTO.class);
    return new ResponseEntity<>(callDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST, value = CALL_REQUEST)
  public ResponseEntity<?> createRequest(
      @Valid @RequestBody CallRequestCreateDTO callRequestCreateDTO,
      @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    CallRequest callRequest = callService.createRequest(callRequestCreateDTO, authUser);

    CallRequestDTO callRequestDTO = modelMapper.map(callRequest, CallRequestDTO.class);
    return new ResponseEntity<>(callRequestDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST, value = CALL)
  public ResponseEntity<?> create(
      @Valid @RequestBody CallCreateDTO callCreateDTO, @AuthenticationPrincipal AuthUser authUser) {
    Call call = callService.create(callCreateDTO, authUser);

    CallDTO callDTO = modelMapper.map(call, CallDTO.class);
    return new ResponseEntity<>(callDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, value = CALL)
  public ResponseEntity<?> update(
      @Valid @RequestBody CallUpdateDTO callUpdateDTO, @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    Call call = callService.update(callUpdateDTO, authUser);

    CallDTO callDTO = modelMapper.map(call, CallDTO.class);
    return new ResponseEntity<>(callDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, value = CALL + "/{id}")
  public ResponseEntity<?> update(
      @PathVariable ObjectId id,
      @Valid @RequestBody CallUpdateDTO callUpdateDTO,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    callUpdateDTO.setId(id);
    Call call = callService.update(callUpdateDTO, authUser);

    CallDTO callDTO = modelMapper.map(call, CallDTO.class);
    return new ResponseEntity<>(callDTO, HttpStatus.OK);
  }
}

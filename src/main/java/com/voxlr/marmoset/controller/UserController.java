package com.voxlr.marmoset.controller;

import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.RemovedEntityDTO;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.PageDTO;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.UserDTO;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.service.domain.UserService;
import com.voxlr.marmoset.util.MapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.voxlr.marmoset.controller.CompanyController.COMPANY;
import static com.voxlr.marmoset.util.ListUtils.listOf;

@RestController
public class UserController extends ApiController {
  public static final String USER = "/user";
  public static final String COMPANY_USER = COMPANY + "/{companyId}" + USER;

  @Autowired private UserService userService;
  @Autowired private MapperUtils mapperUtils;
  @Autowired private ModelMapper modelMapper;
  //    @Autowired private JavaMailSender emailSender;

  @RequestMapping(method = RequestMethod.GET, value = USER + "/{id}")
  public ResponseEntity<?> get(@PathVariable String id, @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    User user = userService.get(id, authUser);
    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
    return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = COMPANY_USER)
  public ResponseEntity<?> getUsersByCompany(
      @PathVariable String companyId, Pageable pageable, @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    Pageable pageContext = page(pageable);
    Page<User> users = userService.getUsersByCompany(companyId, pageContext, authUser);
    PageDTO<UserDTO> userDTOs = mapperUtils.mapPage(users, UserDTO.class);
    return new ResponseEntity<PageDTO<UserDTO>>(userDTOs, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST, value = USER)
  public ResponseEntity<?> create(
      @Valid @RequestBody UserCreateDTO userCreateDTO, @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    User user = userService.create(userCreateDTO, authUser);

    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
    sendNewUserEmail(user);
    return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, value = USER + "/{id}")
  public ResponseEntity<?> update(
      @PathVariable String id,
      @Valid @RequestBody UserUpdateDTO userUpdateDTO,
      @AuthenticationPrincipal AuthUser authUser)
      throws Exception {
    userUpdateDTO.setId(id);
    User user = userService.update(userUpdateDTO, authUser);

    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
    return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = USER + "/{id}")
  public ResponseEntity<?> delete(
      @PathVariable String id, @AuthenticationPrincipal AuthUser authUser)
      throws EntityNotFoundException {
    userService.delete(id, authUser);

    RemovedEntityDTO removedEntityDTO = new RemovedEntityDTO(id);

    return new ResponseEntity<RemovedEntityDTO>(removedEntityDTO, HttpStatus.OK);
  }

  @Override
  public Map<String, List<String>> getSortOptions() {
    Map<String, List<String>> sortMap = new HashMap<>();
    sortMap.put("fullName", listOf("firstName", "lastName"));

    return sortMap;
  }

  private void sendNewUserEmail(User user) {
    //	SimpleMailMessage message = new SimpleMailMessage();
    //	message.setTo(user.getEmail());
    //	message.setSubject("Welcome to Voxlr!");
    //	message.setText("Welcome to Voxlr");
    //	emailSender.send(message);
  }
}

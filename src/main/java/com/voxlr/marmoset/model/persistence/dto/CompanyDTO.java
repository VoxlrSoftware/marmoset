package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CompanyDTO {

  private String id;
  private String name;
  private PhoneNumberHolder phoneNumber;
  private List<CallStrategyDTO> callStrategies = new ArrayList<CallStrategyDTO>() {};
}

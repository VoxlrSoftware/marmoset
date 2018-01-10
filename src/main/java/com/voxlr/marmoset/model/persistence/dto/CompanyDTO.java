package com.voxlr.marmoset.model.persistence.dto;

import java.util.ArrayList;
import java.util.List;

import com.voxlr.marmoset.model.PhoneNumberHolder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    
    private String id;
    private String name;
    private PhoneNumberHolder phoneNumber;
    private List<String> callStrategies = new ArrayList<String>() {
    };
}

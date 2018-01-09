package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    
    private String id;
    private String name;
    private PhoneNumberHolder phoneNumber;
}

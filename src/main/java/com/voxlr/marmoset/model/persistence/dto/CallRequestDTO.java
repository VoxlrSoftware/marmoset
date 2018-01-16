package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallRequestDTO {
    private String id;
    private CallStrategyDTO callStrategy;
    private PhoneNumberHolder employeeNumber;
    private PhoneNumberHolder customerNumber;
    private String userId;
    private String companyId;
}

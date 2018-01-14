package com.voxlr.marmoset.model.persistence.dto;

import java.util.List;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {

    private String id;
    private String name;
    
    @PhoneNumberValidConstraint
    private PhoneNumberHolder phoneNumber;
    
    @Singular
    private List<CallStrategyDTO> callStrategies;
}

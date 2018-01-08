package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {

    private String id;
    private String name;
    
    @PhoneNumberValidConstraint
    private String phoneNumber;
}

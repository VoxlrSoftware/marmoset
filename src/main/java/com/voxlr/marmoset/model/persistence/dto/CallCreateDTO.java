package com.voxlr.marmoset.model.persistence.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

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
public class CallCreateDTO {
    @NotBlank
    private String callSid;
    
    @PhoneNumberValidConstraint
    private String employeeNumber;
    
    @PhoneNumberValidConstraint
    private String customerNumber;

    @Singular("strategy")
    private List<String> strategyList;
}

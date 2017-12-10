package com.voxlr.marmoset.model.persistence.dto;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    @NotBlank
    private String id;
    
    @NotBlank
    private String name;
}

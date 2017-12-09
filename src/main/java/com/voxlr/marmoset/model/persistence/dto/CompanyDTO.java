package com.voxlr.marmoset.model.persistence.dto;

import org.hibernate.validator.constraints.NotBlank;

public class CompanyDTO {
    @NotBlank
    private String id;
    
    @NotBlank
    private String name;
}

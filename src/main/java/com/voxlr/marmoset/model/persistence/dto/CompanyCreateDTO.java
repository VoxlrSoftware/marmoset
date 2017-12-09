package com.voxlr.marmoset.model.persistence.dto;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCreateDTO {

    @NotBlank
    private String name;
}

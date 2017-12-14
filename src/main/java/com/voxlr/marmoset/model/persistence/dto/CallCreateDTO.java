package com.voxlr.marmoset.model.persistence.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallCreateDTO {
    private String callSid;
    private String employeeNumber;
    private String customerNumber;
    private List<String> strategyList = new ArrayList<>();
}

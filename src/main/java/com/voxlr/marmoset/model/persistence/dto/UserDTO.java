package com.voxlr.marmoset.model.persistence.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.voxlr.marmoset.model.PhoneNumberHolder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String companyId;

    private String teamId;
    
    private String firstName;
    
    private String lastName;
    
    private String fullName;

    private String email;
    
    private String id;
    
    private String role;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createDate;
    
    private PhoneNumberHolder phoneNumber;
}

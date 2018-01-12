package com.voxlr.marmoset.model.persistence.dto;

import java.util.Date;

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
    
    private Date createDate;
    
    private PhoneNumberHolder phoneNumber;
}

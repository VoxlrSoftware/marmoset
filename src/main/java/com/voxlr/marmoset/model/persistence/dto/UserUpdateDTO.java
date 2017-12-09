package com.voxlr.marmoset.model.persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String teamId;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private String id;
}

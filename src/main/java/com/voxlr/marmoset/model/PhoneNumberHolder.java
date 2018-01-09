package com.voxlr.marmoset.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumberHolder {
    private String number = "";
    private String extension = "";
    
    public PhoneNumberHolder() {
	
    }
    
    public PhoneNumberHolder(String number) {
	this.number = number;
    }
}

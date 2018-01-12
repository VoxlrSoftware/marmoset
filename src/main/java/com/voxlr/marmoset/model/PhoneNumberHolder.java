package com.voxlr.marmoset.model;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.voxlr.marmoset.util.CompareUtils.safeEquals;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneNumberHolder {
    private String number = "";
    private String extension = "";
    
    public PhoneNumberHolder(String number) {
	this.number = number;
    }
    
    public boolean hasExtension() {
	return !isNullOrEmpty(extension);
    }
    
    public static boolean comparePhoneNumbers(PhoneNumberHolder phoneNumber1, PhoneNumberHolder phoneNumber2) {
	return phoneNumber1 != null &&
		phoneNumber2 != null &&
		safeEquals(phoneNumber1.getNumber(), phoneNumber2.getNumber()) &&
		safeEquals(phoneNumber1.getExtension(), phoneNumber2.getExtension());
    }
}

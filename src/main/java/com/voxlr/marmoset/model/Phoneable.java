package com.voxlr.marmoset.model;

public interface Phoneable<T> extends GlobalEntity {
    PhoneNumberHolder getPhoneNumber();
    T setPhoneNumber(PhoneNumberHolder phoneNumber);
}

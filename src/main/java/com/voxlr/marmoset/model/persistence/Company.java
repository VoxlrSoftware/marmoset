package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.Phoneable;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

@Document(collection = "companies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Company extends AuditModel implements Phoneable<Company> {
    
    private String name;
    
    @PhoneNumberValidConstraint
    private PhoneNumberHolder phoneNumber;
    
    @Singular
    private List<CallStrategy> callStrategies = new ArrayList<CallStrategy>();
}

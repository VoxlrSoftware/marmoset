package com.voxlr.marmoset.model.persistence;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.model.CompanyScopedEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Document(collection = "teams")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class Team extends AuditModel implements CompanyScopedEntity {
    
    @NotNull
    private String name;
    
    @NotNull
    @Indexed
    private String companyId;
}

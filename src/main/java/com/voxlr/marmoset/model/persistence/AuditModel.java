package com.voxlr.marmoset.model.persistence;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class AuditModel extends Entity {
    public static final String CREATE_DATE = "createDate";
    public static final String LAST_MODIFIED = "lastModified";
    
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field(CREATE_DATE)
    private DateTime createDate;
    
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field(LAST_MODIFIED)
    private DateTime lastModified;
}

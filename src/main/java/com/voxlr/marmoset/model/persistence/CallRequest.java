package com.voxlr.marmoset.model.persistence;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.UserScopedEntity;
import com.voxlr.marmoset.service.CallbackService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "callRequests")
@EnableMongoAuditing
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallRequest extends Entity implements UserScopedEntity {
    private String userId;

    @Field("empNum")
    private PhoneNumberHolder employeeNumber;
    
    @Field("custNum")
    private PhoneNumberHolder customerNumber;
    
    private CallStrategy callStrategy;
    
    private CallbackService.Platform platform;
    
    @Indexed(name = "timeToLive", expireAfterSeconds = 300)
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createDate;
}

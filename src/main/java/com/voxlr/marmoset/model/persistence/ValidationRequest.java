package com.voxlr.marmoset.model.persistence;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.UserScopedEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "validationRequests")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "userId_entityId", def = "{'userId' : 1, 'entityId': 1}"),
})
public class ValidationRequest extends Entity implements UserScopedEntity {
    private String userId;
    private String entityId;
    private String entityType;
    
    @Indexed
    private String requestId;
    
    private PhoneNumberHolder phoneNumber;

    @Builder.Default
    private boolean hasValidated = false;
    
    @Builder.Default
    private boolean isValid = false;

    @Indexed(name = "timeToLive", expireAfterSeconds = 3600)
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createDate;
}

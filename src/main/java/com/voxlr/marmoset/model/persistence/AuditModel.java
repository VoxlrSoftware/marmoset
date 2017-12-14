package com.voxlr.marmoset.model.persistence;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import com.voxlr.marmoset.model.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EnableMongoAuditing
@Getter
@Setter
public abstract class AuditModel implements Entity {
    @Id
    private String id;
    
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createDate;
    
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastModified;
    
    public String getId() {
	return id;
    }
    
    public void setId(String id) {
	this.id = id;
    }
    
    public Date getCreateDate() {
	return createDate;
    }
    
    public Date getLastModifiedDate() {
	return lastModified;
    }
}

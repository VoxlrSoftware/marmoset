package com.voxlr.marmoset.model.persistence;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class CallStrategy {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    
    @NotNull
    private String phrase;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date modifiedDate;

    public CallStrategy(String id, String phrase, Date createDate, Date modifiedDate) {
	this.id = id;
	this.phrase = phrase;
	this.createDate = createDate;
	this.modifiedDate = modifiedDate;
    }
}

package com.voxlr.marmoset.model.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallbackResult {
    @JsonIgnore
    private HttpStatus status = HttpStatus.OK;
    private String result = "Callback accepted.";
    
    public CallbackResult(String result) {
	this.result = result;
    }
    
    public CallbackResult(String result, HttpStatus status) {
	this.result = result;
	this.status = status;
    }
}

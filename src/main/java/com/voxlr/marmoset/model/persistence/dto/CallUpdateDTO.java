package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.CallScoped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallUpdateDTO implements CallScoped {
    private String id;
    private String callSid;
    private String callOutcome;
}

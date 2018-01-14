package com.voxlr.marmoset.model.persistence.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

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
public class CallStrategyDTO {
    private String id;
    
    @NotNull
    private String name;
    
    @NotNull
    private List<String> phrases;
}

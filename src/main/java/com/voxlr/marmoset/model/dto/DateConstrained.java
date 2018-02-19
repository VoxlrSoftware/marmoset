package com.voxlr.marmoset.model.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateConstrained {
    @NotNull(groups = {StartDateConstrained.class, DateConstrained.class})
    private Date startDate;
    
    @NotNull(groups = {EndDateConstrained.class, DateConstrained.class})
    private Date endDate;
    
    public interface StartDateConstrained {}
    
    public interface EndDateConstrained {}
}

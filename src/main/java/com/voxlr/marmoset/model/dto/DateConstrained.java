package com.voxlr.marmoset.model.dto;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

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
    private DateTime startDate;
    
    @NotNull(groups = {EndDateConstrained.class, DateConstrained.class})
    private DateTime endDate;
    
    public interface StartDateConstrained {}
    
    public interface EndDateConstrained {}
}

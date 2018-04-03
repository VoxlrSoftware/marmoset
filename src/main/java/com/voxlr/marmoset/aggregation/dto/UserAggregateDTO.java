package com.voxlr.marmoset.aggregation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAggregateDTO {
  private String id;
  @JsonIgnore private String firstName;
  @JsonIgnore private String lastName;
  private Integer totalCalls;
  private Integer totalTalkTime;
  private Integer totalConversations;
  private Double detectionRatio;
  private Double customerTalkRatio;
  private Double conversationRatio;


  @JsonProperty("fullName")
  public String getFullName() {
    String fullName = null;

    if (firstName != null) {
      fullName = firstName;
    }

    if (lastName != null) {
      fullName = fullName != null ? fullName + " " + lastName : lastName;
    }

    return fullName;
  }
}

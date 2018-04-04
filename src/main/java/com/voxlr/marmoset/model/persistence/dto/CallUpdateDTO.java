package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.CallScoped;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallUpdateDTO implements CallScoped {
  private ObjectId id;
  private String callSid;
  private String callOutcome;
}

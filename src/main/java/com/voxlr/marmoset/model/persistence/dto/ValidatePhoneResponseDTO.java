package com.voxlr.marmoset.model.persistence.dto;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidatePhoneResponseDTO {
  private String id;

  private PhoneNumberHolder phoneNumberHolder;

  private int validationCode;

  @Builder.Default private boolean hasValidated = false;

  @Builder.Default private boolean isValid = false;

  public static ValidatePhoneResponseDTO buildValidResponse() {
    return ValidatePhoneResponseDTO.builder().hasValidated(true).isValid(true).build();
  }
}

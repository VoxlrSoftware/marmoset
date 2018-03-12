package com.voxlr.marmoset.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CallbackException extends Exception {
  private static final long serialVersionUID = 2038568106940491455L;

  private HttpStatus status = HttpStatus.BAD_REQUEST;

  public CallbackException(String message, Throwable exception) {
    super(message, exception);
  }
}

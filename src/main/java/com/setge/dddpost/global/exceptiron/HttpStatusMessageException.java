package com.setge.dddpost.global.exceptiron;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpStatusMessageException extends RuntimeException {

  private final HttpStatus status;
  private final String code;
  private final Object[] args;

  public HttpStatusMessageException(
      HttpStatus status,
      String code,
      Object... args
  ) {
    super(code);
    this.status = status;
    this.code = code;
    this.args = args;
  }

  public HttpStatusMessageException(
      HttpStatus status,
      String code,
      Throwable t,
      Object... args
  ) {
    super(code, t);
    this.status = status;
    this.code = code;
    this.args = args;
  }
  
}

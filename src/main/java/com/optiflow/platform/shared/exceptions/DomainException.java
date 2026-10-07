package com.optiflow.platform.shared.exceptions;

public class DomainException extends RuntimeException {

  private final int status;

  public DomainException(String message, int status) {
    super(message);
    this.status = status;
  }

  public int status() {
    return status;
  }
}

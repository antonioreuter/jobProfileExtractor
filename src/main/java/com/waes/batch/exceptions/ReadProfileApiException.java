package com.waes.batch.exceptions;

/**
 * Created by antonioreuter on 07/02/17.
 */
public class ReadProfileApiException extends RuntimeException {

  public ReadProfileApiException(String message) {
    super(message);
  }

  public ReadProfileApiException(String message, Throwable th) {
    super(message,th);
  }
}

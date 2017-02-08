package com.waes.batch.exceptions;

/**
 * Created by antonioreuter on 07/02/17.
 */
public class RetrieveDetailedInfoException extends RuntimeException {

  public RetrieveDetailedInfoException(String message) {
    super(message);
  }

  public RetrieveDetailedInfoException(String message, Throwable th) {
    super(message,th);
  }
}

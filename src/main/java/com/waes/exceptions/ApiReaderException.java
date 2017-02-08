package com.waes.exceptions;

/**
 * Created by antonioreuter on 08/02/17.
 */
public class ApiReaderException extends RuntimeException {

  public ApiReaderException(String msg) {
    super(msg);
  }

  public ApiReaderException(String msg, Throwable th) {
    super(msg, th);
  }
}

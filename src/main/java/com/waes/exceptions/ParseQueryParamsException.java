package com.waes.exceptions;

/**
 * Created by antonioreuter on 08/02/17.
 */
public class ParseQueryParamsException extends RuntimeException {

  public ParseQueryParamsException(String msg) {
    super(msg);
  }

  public ParseQueryParamsException(String msg, Throwable th) {
    super(msg, th);
  }
}

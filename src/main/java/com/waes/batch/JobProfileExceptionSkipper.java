package com.waes.batch;

import com.waes.batch.exceptions.RetrieveDetailedInfoException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * Created by antonioreuter on 07/02/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class JobProfileExceptionSkipper implements SkipPolicy {

  private Integer threshold;

  @Override
  public boolean shouldSkip(Throwable th, int skipCount) throws SkipLimitExceededException {
    if (th instanceof RetrieveDetailedInfoException && skipCount <= threshold) {
      log.error(th.getMessage(), th);
      return true;
    }
    return false;
  }
}

package com.waes.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
/**
 * Created by antonioreuter on 03/02/17.
 */
@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("Job Status : " + jobExecution.getStatus());
  }
}
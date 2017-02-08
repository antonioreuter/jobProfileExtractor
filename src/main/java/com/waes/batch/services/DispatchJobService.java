package com.waes.batch.services;

import com.waes.exceptions.InvalidSourceException;
import com.waes.models.JobProfileQuery;
import com.waes.services.ProcessDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Slf4j
@Service("dispatchJobService")
public class DispatchJobService implements ProcessDataService {

  @Autowired
  private JobLauncher jobLauncher;

  @Qualifier("githubExtractorJobProfileJob")
  @Autowired
  private Job githubExtractorJobProfileJob;

  public void process(JobProfileQuery jobProfileQuery) {
    log.debug("Starting the batch job with the follow params: {}", jobProfileQuery);

    Job selectedJob = selectJob(jobProfileQuery.getSource());
    log.debug("Selected JOB: "+ selectedJob);

    try {
      jobLauncher.run(selectedJob, jobParams(jobProfileQuery));
    } catch (Exception ex) {
      throw new RuntimeException("Wasn't possible to process the job", ex);
    }
  }

  private JobParameters jobParams(JobProfileQuery queryParams) {
    return new JobParametersBuilder()
        .addString("apiUrl", queryParams.getQueryUri())
        .addString("selectedFields",queryParams.getSelectedFields())
        .addString("outputFile", queryParams.getOutputFilePath())
        .toJobParameters();
  }

  private Job selectJob(String source) {
    Job selected = null;
    switch (source.trim().toUpperCase()) {
      case "GITHUB": selected = githubExtractorJobProfileJob;
        break;
      default: throw new InvalidSourceException("The selected source type wasn't implemented yet.");
    }

    return selected;
  }
}

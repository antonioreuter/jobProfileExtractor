package com.waes;

import com.waes.batch.jobs.GithubJobProfileExtractorJob;
import com.waes.config.ApplicationConfig;
import com.waes.models.JobProfileQuery;
import com.waes.services.ProcessDataService;
import com.waes.util.ProcessDataArguments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by antonioreuter on 02/02/17.
 */

@Slf4j
public class Application {

  public static void main(String[] args) throws Exception {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.register(ApplicationConfig.class);
    context.register(GithubJobProfileExtractorJob.class);
    context.refresh();

    log.info("Start execution...");
    ProcessDataService processDataService = (ProcessDataService) context.getBean("dispatchJobService");
    JobProfileQuery jobProfileQuery = (new ProcessDataArguments(args)).queryParams();
    processDataService.process(jobProfileQuery);
  }
}

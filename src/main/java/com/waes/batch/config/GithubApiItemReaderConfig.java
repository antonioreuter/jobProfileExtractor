package com.waes.batch.config;

import com.waes.batch.models.github.JobProfile;
import com.waes.batch.services.github.GithubSearchUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Slf4j
@Configuration
public class GithubApiItemReaderConfig {

  @Autowired
  private GithubSearchUsersService searchUsersService;

  @StepScope
  @Bean(name = "githubApiItemReader")
  ListItemReader<JobProfile> apiReader(@Value("#{jobParameters[apiUrl]}") String apiUrl) {
    List<JobProfile> source = searchUsersService.search(apiUrl);
    log.info("Total of records found: {}", source.size());
    ListItemReader<JobProfile> apiItemReader = new ListItemReader<>(source);

    return apiItemReader;
  }
}

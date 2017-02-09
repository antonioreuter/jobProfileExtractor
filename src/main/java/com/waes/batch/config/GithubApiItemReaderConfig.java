package com.waes.batch.config;

import com.waes.batch.models.github.JobProfile;
import com.waes.batch.readers.github.dto.GithubUserResultSet;
import com.waes.batch.services.github.GithubSearchUsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Slf4j
@Configuration
public class GithubApiItemReaderConfig {

  @Autowired
  private GithubSearchUsersService searchUsersService;

  @Value("${github.api.max_per_page}")
  private Integer maxPerPage;

  @StepScope
  @Bean(name = "githubApiItemReader")
  ListItemReader<JobProfile> apiReader(@Value("#{jobParameters[apiUrl]}") String apiUrl) {
    List<JobProfile> source = retrieveData(apiUrl);
    if (CollectionUtils.isEmpty(source))
      throw new RuntimeException("There is no data to be processed!");

    log.info("Total of records found: {}", source.size());
    ListItemReader<JobProfile> apiItemReader = new ListItemReader<>(source);

    return apiItemReader;
  }

  private List<JobProfile> retrieveData(String apiUrl) {
    Integer index = 0;
    List<JobProfile> source = new ArrayList<>();
    boolean keepSearching = true;

    while (keepSearching) {
      GithubUserResultSet resultSet = searchUsersService.search(apiUrl,index);
      if (resultSet != null && CollectionUtils.isNotEmpty(resultSet.getItems())) {
        source.addAll(resultSet.getItems());
      }

      keepSearching = (resultSet != null && CollectionUtils.isNotEmpty(resultSet.getItems()) && (resultSet.getItems().size() == 100) );
      index++;
    }
    return source;
  }
}

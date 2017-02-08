package com.waes.batch.services.github;


import com.waes.batch.exceptions.ReadProfileApiException;
import com.waes.batch.exceptions.RetrieveDetailedInfoException;
import com.waes.batch.readers.github.dto.GithubUserResultSet;
import com.waes.batch.models.github.JobProfile;
import com.waes.batch.models.github.Repo;
import com.waes.exceptions.ApiReaderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by antonioreuter on 06/02/17.
 */

@Slf4j
@Service("gitubSearchUsersService")
public class GithubSearchUsersService {

  @Autowired
  @Qualifier("githubRestTemplate")
  private RestTemplate restTemplate;

  public List<JobProfile> search(String query) {
    ResponseEntity<GithubUserResultSet> response = null;

    try {
      response = restTemplate.getForEntity(query, GithubUserResultSet.class);
    } catch (HttpClientErrorException ex) {
      throw new ApiReaderException(ex.getResponseBodyAsString(), ex);
    }

    if (response.getStatusCode() != HttpStatus.OK)
      throw new ReadProfileApiException("Wasn't possible to perform this query!");

    GithubUserResultSet resultSet = response.getBody();

    return resultSet.getItems();
  }

  public JobProfile retrieveJobProfileBioInfo(JobProfile jobProfile) {
    try {
      ResponseEntity<JobProfile> response = restTemplate.getForEntity("https://api.github.com/users/{login}", JobProfile.class, jobProfile.getLogin());
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        jobProfile = response.getBody();
      }
    } catch (Exception ex) {
      throw new RetrieveDetailedInfoException(String.format("Error when tried to retrieve the jobProfile: %s", jobProfile), ex);
    }

    return jobProfile;
  }

  public List<Repo> retrieveJobProfileRepos(JobProfile jobProfile) {
    List<Repo> result = null;
    try {
      ResponseEntity<Repo[]> response = restTemplate.getForEntity(jobProfile.getReposUrl(), Repo[].class);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        result = Arrays.asList(response.getBody());
      }
    } catch (Exception ex) {
      throw new RetrieveDetailedInfoException(String.format("Error when tried to retrieve the repositories for the jobProfile: %s", jobProfile.getReposUrl()), ex);
    }

    return result;
  }

  public Map<String, String> retrieveProgrammingLanguagesFromRepo(Repo repo) {
    Map<String, String> result = null;
    try {
      ResponseEntity<Map> response = restTemplate.getForEntity(repo.getLanguagesUrl(), Map.class);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        result = response.getBody();
      }
    } catch (Exception ex) {
      throw new RetrieveDetailedInfoException(String.format("Error when tried to retrieve the programming languages used in the repo: %s", repo.getLanguagesUrl()), ex);
    }

    return result;
  }
}

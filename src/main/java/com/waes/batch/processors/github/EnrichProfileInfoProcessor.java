package com.waes.batch.processors.github;

import com.google.common.collect.Sets;
import com.waes.batch.models.github.JobProfile;
import com.waes.batch.models.github.Repo;
import com.waes.batch.services.github.GithubSearchUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by antonioreuter on 06/02/17.
 */
@Slf4j
@Component("enrichBioProfileInfoProcessor")
public class EnrichProfileInfoProcessor implements ItemProcessor<JobProfile, JobProfile> {

  @Autowired
  private GithubSearchUsersService searchUsersService;

  @Override
  public JobProfile process(JobProfile jobProfile) throws Exception {
    log.debug("@@@@@ Enriching JobProfile {}",jobProfile);
    jobProfile = searchUsersService.retrieveJobProfileBioInfo(jobProfile);
    summarizeRepoInfo(jobProfile);

    return jobProfile;
  }

  private void summarizeRepoInfo(JobProfile jobProfile) {
    List<Repo> repos = searchUsersService.retrieveJobProfileRepos(jobProfile);

    Integer totalStars = 0;
    Integer totalWatchers = 0;
    Integer totalRepos = 0;
    Date lastCommit = null;
    Set<String> languages = new HashSet<>();
    String lastRepoCommited = "-";

    if (!repos.isEmpty()) {
      totalRepos = repos.size();

      for (Repo repo : repos) {
        totalStars += repo.getStars();
        totalWatchers += repo.getWatchers();

        Set<String> retrievedLanguages = retrieveProgrammingLanguages(repo);
        if (!StringUtils.isEmpty(retrievedLanguages)) {
          languages.addAll(retrievedLanguages);
        }

        if (lastCommit == null || lastCommit.compareTo(repo.getPushedAt()) < 0) {
          lastCommit = repo.getPushedAt();
          lastRepoCommited = repo.getName();
        }
      }
    }

    jobProfile.setTotalStars(totalStars);
    jobProfile.setTotalWatchers(totalWatchers);
    jobProfile.setLastCommit(lastCommit);
    jobProfile.setLastRepoCommited(lastRepoCommited);
    jobProfile.setTotalRepos(totalRepos);

    if (!languages.isEmpty()) {
      jobProfile.setProgrammingLanguages(String.format("[%s]", String.join(",", languages)));
    }
  }

  private Set<String> retrieveProgrammingLanguages(Repo repo) {
    Set<String> result = null;
    Map<String, String> langs = searchUsersService.retrieveProgrammingLanguagesFromRepo(repo);

    if (langs != null && !langs.isEmpty()) {
      result = langs.keySet();
    } else if (!StringUtils.isEmpty(repo.getLanguage())) {
      result = Sets.newHashSet(repo.getLanguage());
    }

    return result;
  }
}

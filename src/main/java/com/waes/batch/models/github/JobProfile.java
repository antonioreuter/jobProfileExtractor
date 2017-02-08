package com.waes.batch.models.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by antonioreuter on 02/02/17.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "login"})
@ToString(of = {"id", "login"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobProfile implements Serializable {

  private Long id;

  private String name;

  private String login;

  private String email;

  private String location;

  @JsonProperty("public_repos")
  private Integer publicRepos;

  private Integer followers;

  private String type;

  private boolean hireable;

  private Double score;

  private List<Repo> repos;

  private String blog;

  private String bio;

  private String company;

  private Integer totalStars;

  private Integer totalWatchers;

  private Integer totalRepos;

  private Date lastCommit;

  private String lastRepoCommited;

  private String programmingLanguages;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  private String url;

  @JsonProperty("repos_url")
  private String reposUrl;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;
}

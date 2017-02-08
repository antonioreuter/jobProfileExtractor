package com.waes.batch.models.github;

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
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"id", "name"})
public class Repo implements Serializable {

  private Long id;

  private String name;

  private String url;

  @JsonProperty("stargazers_count")
  private Integer stars;

  @JsonProperty("watchers_count")
  private Integer watchers;

  private String language;

  private List<String> languages;

  @JsonProperty("languages_url")
  private String languagesUrl;

  @JsonProperty("pushed_at")
  private Date pushedAt;
}

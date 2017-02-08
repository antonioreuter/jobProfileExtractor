package com.waes.batch.readers.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.waes.batch.models.github.JobProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubUserResultSet implements Serializable {
  @JsonProperty("total_count")
  private Integer totalCount;

  @JsonProperty("incomplete_results")
  private boolean incompleteResults;

  @JsonProperty("items")
  private List<JobProfile> items;
}

package com.waes.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by antonioreuter on 08/02/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobProfileQuery {
  @JsonProperty("query_uri")
  private String queryUri;

  @JsonProperty("selected_fields")
  private String selectedFields;

  private String source;

  private String outputFilePath;
}

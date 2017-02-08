package com.waes.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.exceptions.ParseQueryParamsException;
import com.waes.models.JobProfileQuery;
import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * Created by antonioreuter on 07/02/17.
 */
@Data
public class ProcessDataArguments {
  private CommandLine commandLine;

  public ProcessDataArguments(String... params) throws ParseException{
    this.commandLine = CommandLineUtil.convertArgToCommandLine(params);
  }

  public JobProfileQuery queryParams() {
    String queryFilePath = queryFilePathOption();
    if (!queryFilePath.endsWith(".json"))
      throw new IllegalArgumentException("The file with the query params must be a json file.");

    if (commandLine.hasOption("query") && !StringUtils.isEmpty(queryFilePath)) {
      ObjectMapper mapper = new ObjectMapper();
      JobProfileQuery jobProfileQuery = null;
      try {
        jobProfileQuery  = mapper.readValue(new File(queryFilePath), JobProfileQuery.class);
        jobProfileQuery.setOutputFilePath(outputFilePath());
      } catch (IOException ex) {
        throw new ParseQueryParamsException("Wasn't possible to read the query params.");
      }
      return jobProfileQuery;
    } else {
      throw new IllegalArgumentException("You need to inform the query to find the job profiles.");
    }
  }

  private String outputFilePath() {
    String outputFilePath = outputFilePathOption();
    if (StringUtils.isEmpty(outputFilePath)) {
      File file = new File(queryFilePathOption());
      String outputFileName = getOutputFileName();
      outputFilePath = new File(file.getParent(), outputFileName).getPath();
    }

    return outputFilePath;
  }

  private String getOutputFileName() {
    String fileName = new File(queryFilePathOption()).getName();
    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    String extension = "csv";
    Long timestamp = Instant.now().toEpochMilli();
    return String.format("output_%s_%s.%s", fileName, timestamp, extension);
  }

  private String outputFilePathOption() {
    return commandLine.getOptionValue("output");
  }

  private String queryFilePathOption(){
    return commandLine.getOptionValue("query");
  }
}

package com.waes.util;

import org.apache.commons.cli.*;

/**
 * Created by antonioreuter on 07/02/17.
 */

public class CommandLineUtil {

  public static CommandLine convertArgToCommandLine(String... args) throws ParseException {
    CommandLineParser cmdParser = new DefaultParser();
    return cmdParser.parse(buildOptions(), args);
  }

  private static Options buildOptions() {
    Option queryOption = Option.builder("q")
        .longOpt("query")
        .numberOfArgs(1)
        .required(true)
        .desc("source file the query params")
        .build();

    Option outputOption = Option.builder("o")
        .longOpt("output")
        .numberOfArgs(1)
        .required(false)
        .desc("path of the output file")
        .build();

    Options options = new Options();
    options.addOption(queryOption);
    options.addOption(outputOption);

    return options;
  }
}

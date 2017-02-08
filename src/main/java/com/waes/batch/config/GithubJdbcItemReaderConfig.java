package com.waes.batch.config;

import com.waes.batch.models.github.JobProfile;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.StringUtils;

/**
 * Created by antonioreuter on 03/02/17.
 */

//@Configuration
public class GithubJdbcItemReaderConfig {

  private static final String QUERY_FORMAT = "Select name,login,location From JobProfile %s";

  @Autowired
  private DataSource dataSource;

  @StepScope
  @Bean(name = "jdbcReader")
  JdbcCursorItemReader<JobProfile> jdbcReader(@Value("#{jobParameters[orderBy]}") String orderBy) {
    JdbcCursorItemReader<JobProfile> databaseReader = new JdbcCursorItemReader<>();
    databaseReader.setDataSource(dataSource);
    databaseReader.setRowMapper(new BeanPropertyRowMapper<>(JobProfile.class));

    if (StringUtils.isEmpty(orderBy))
      databaseReader.setSql(String.format(QUERY_FORMAT, ""));
    else
      databaseReader.setSql(String.format(QUERY_FORMAT, "order by "+orderBy));

    return databaseReader;
  }
}

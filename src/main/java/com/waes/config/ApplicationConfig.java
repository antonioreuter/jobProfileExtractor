package com.waes.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = { "com.waes.*" })
@PropertySource(value="classpath:/application.properties", ignoreResourceNotFound=true)
public class ApplicationConfig {

  @Value("${http.connection.timeout}")
  private int connectionTimeout;

  @Value("${batch.concurrency}")
  private Integer concurrency;

  @Value("${github.api.login}")
  private String githubLogin;

  @Value("${github.api.password}")
  private String githubPassword;

  @Bean(name = "githubRestTemplate")
  RestTemplate githubRestTemplate(@Qualifier("httpClient") HttpClient httpClient) {
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
    restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(githubLogin, githubPassword));

    return restTemplate;
  }

  @Bean(name = "httpClient")
  public HttpClient httpClient() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(5);
    connectionManager.setMaxTotal(concurrency);

    int timeout = connectionTimeout * 1000;

    RequestConfig config = RequestConfig.custom()
        .setSocketTimeout(timeout)
        .setConnectTimeout(timeout)
        .setConnectionRequestTimeout(timeout)
        .build();

    return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).build();
  }

  @Bean
  public DataSource dataSource() {
    EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
    javax.sql.DataSource ds = embeddedDatabaseBuilder
        .addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
        .addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
        .setType(EmbeddedDatabaseType.HSQL)
        .build();

    DataSource dataSource = new DataSource();
    dataSource.setDataSource(ds);
    dataSource.setInitialSize(5);
    dataSource.setMaxWait(6000);

    return dataSource;
  }

  @Bean(name = "jobLauncher")
  public JobLauncher jobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository());
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  private JobRepository jobRepository() throws Exception{
    JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
    factoryBean.setDataSource(dataSource());
    factoryBean.setTransactionManager(platformTransactionManager());
    factoryBean.afterPropertiesSet();
    return factoryBean.getObject();
  }

  private PlatformTransactionManager platformTransactionManager() {
    return new ResourcelessTransactionManager();
  }
}

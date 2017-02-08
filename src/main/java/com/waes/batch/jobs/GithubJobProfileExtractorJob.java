package com.waes.batch.jobs;

import com.waes.batch.JobProfileExceptionSkipper;
import com.waes.batch.listeners.JobCompletionNotificationListener;
import com.waes.batch.models.github.JobProfile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Created by antonioreuter on 03/02/17.
 */

@Configuration
public class GithubJobProfileExtractorJob {
  @Value("${batch.concurrency}")
  private Integer concurrency;

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  @Qualifier("githubApiItemReader")
  private ListItemReader<JobProfile> githubApiItemReader;

  @Autowired
  @Qualifier("enrichBioProfileInfoProcessor")
  private ItemProcessor<JobProfile, JobProfile> enrichBioProfileInfoProcessor;

  @Bean(name = "githubExtractorJobProfileJob")
  public Job githubExtractorJobProfileJob(JobCompletionNotificationListener listener, @Qualifier("githubExtractorJobProfileStep") Step extractJobProfileStep) {
    return jobBuilderFactory.get("githubExtractorJobProfileJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(extractJobProfileStep)
        .end()
        .build();
  }

  @Bean(name = "githubExtractorJobProfileStep")
  public Step githubExtractorJobProfileStep(@Qualifier("csvItemWriter") ItemWriter<JobProfile> csvItemWriter) {
    return stepBuilderFactory.get("githubExtractorJobProfileStep")
        .<JobProfile, JobProfile> chunk(10)
        .reader(githubApiItemReader)
        .processor(enrichBioProfileInfoProcessor)
        .faultTolerant()
        .skipPolicy(jobProfileExceptionSkipper())
        .writer(csvItemWriter)
        .taskExecutor(taskExecutor())
        .throttleLimit(concurrency)
        .build();
  }

  @StepScope
  @Bean(name = "csvItemWriter")
  public FlatFileItemWriter<JobProfile> csvItemWriter(@Value("#{jobParameters[outputFile]}") String filePath,
                                                      @Value("#{jobParameters[selectedFields]}") String selectedFields) {
    FlatFileItemWriter<JobProfile> writer = new FlatFileItemWriter<>();
    writer.setShouldDeleteIfEmpty(true);
    writer.setShouldDeleteIfExists(true);
    writer.setAppendAllowed(false);
    writer.setResource(new FileSystemResource(filePath));
    writer.setLineAggregator(createGitHubJobProfileLineAggregator(selectedFields));
    writer.setHeaderCallback(fileWriter -> fileWriter.write(selectedFields));

    return writer;
  }

  @Bean(name = "jobProfileExceptionSkipper")
  public SkipPolicy jobProfileExceptionSkipper() {
    return new JobProfileExceptionSkipper();
  }

  private LineAggregator<JobProfile> createGitHubJobProfileLineAggregator(String selectedFields) {
    DelimitedLineAggregator<JobProfile> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setDelimiter(",");

    FieldExtractor<JobProfile> fieldExtractor = createJobProfileFieldExtractor(selectedFields);
    lineAggregator.setFieldExtractor(fieldExtractor);

    return lineAggregator;
  }

  private FieldExtractor<JobProfile> createJobProfileFieldExtractor(String selectedFields) {
    BeanWrapperFieldExtractor<JobProfile> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(selectedFields.split(","));
    return extractor;
  }

  private TaskExecutor taskExecutor() {
    SimpleAsyncTaskExecutor task = new SimpleAsyncTaskExecutor();
    task.setConcurrencyLimit(concurrency);
    return task;
  }
}

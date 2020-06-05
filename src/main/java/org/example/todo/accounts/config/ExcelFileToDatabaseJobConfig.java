package org.example.todo.accounts.config;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.batch.UserExcelRowMapper;
import org.example.todo.accounts.batch.UserProcessor;
import org.example.todo.accounts.batch.UserWriter;
import org.example.todo.accounts.model.User;
import org.example.todo.common.dto.UserDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Paths;

@Configuration
@Slf4j
public class ExcelFileToDatabaseJobConfig {

	private static final int BATCH_PROCESSING_SIZE = 10; //TODO: set via properties file

	@Bean
	@StepScope
	ItemReader<UserDto> excelUserReader(@Value("#{jobParameters['fileLocation']}") String fileLocation) throws Exception {
		log.info("FILE LOCATION: {}", fileLocation);
		PoiItemReader<UserDto> reader = new PoiItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(Paths.get(fileLocation)));
		reader.setRowMapper(excelRowMapper());
		reader.afterPropertiesSet();
		reader.open(new ExecutionContext());
		return reader;
	}

	private static RowMapper<UserDto> excelRowMapper() {
		return new UserExcelRowMapper();
	}

	@Bean
	ItemProcessor<UserDto, User> excelUserProcessor() {
		return new UserProcessor();
	}

	@Bean
	ItemWriter<User> excelUserWriter() {
		return new UserWriter();
	}

	@Bean
	Step excelFileToDatabaseStep(ItemReader<UserDto> excelUserReader,
	                             ItemProcessor<UserDto, User> excelUserProcessor,
	                             ItemWriter<User> excelUserWriter,
	                             StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("excelFileToDatabaseStep")
				.<UserDto, User>chunk(BATCH_PROCESSING_SIZE)
				.reader(excelUserReader)
				.processor(excelUserProcessor)
				.writer(excelUserWriter)
				.build();
	}

	@Bean
	Job excelFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
	                           @Qualifier("excelFileToDatabaseStep") Step excelUserStep) {
		return jobBuilderFactory.get("excelFileToDatabaseJob")
				.incrementer(new RunIdIncrementer())
				.flow(excelUserStep)
				.end()
				.build();
	}
}

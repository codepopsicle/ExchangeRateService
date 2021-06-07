package com.scalabale.springboot;

import com.scalabale.springboot.executor.ScheduledRateUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@EnableScheduling
public class Application {

	@Autowired
	private ScheduledRateUpdater scheduledRateUpdater;

	@Bean
	public ScheduledExecutorService ScheduledExchangeRateUpdaterService() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		ScheduledFuture returnedFuture = scheduler.scheduleAtFixedRate(scheduledRateUpdater, 0, 3600, TimeUnit.SECONDS);
		return scheduler;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


}

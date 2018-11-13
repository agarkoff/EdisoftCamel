package ru.misterparser.edisoftcamel;

import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ZipAggregationStrategy zipAggregationStrategy() {
        return new ZipAggregationStrategy(true, true);
    }
}

package com.smart.financial;

import com.smart.financial.configuration.AppConfiguration;
import com.smart.financial.configuration.AppProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
@MapperScan(basePackages = "com.smart.financial.dao")
public class SmartFinancialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFinancialApplication.class, args);
    }

}

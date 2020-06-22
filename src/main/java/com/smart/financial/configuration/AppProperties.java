package com.smart.financial.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "smart")
public class AppProperties {

    private String tushareUrl;
    private String tushareToken;


    public String getTushareUrl() {
        return tushareUrl;
    }

    public void setTushareUrl(String tushareUrl) {
        this.tushareUrl = tushareUrl;
    }

    public String getTushareToken() {
        return tushareToken;
    }

    public void setTushareToken(String tushareToken) {
        this.tushareToken = tushareToken;
    }
}

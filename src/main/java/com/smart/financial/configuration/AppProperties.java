package com.smart.financial.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

package com.example.meituan.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@Configuration
public class RestClientConfig {

    @Resource
    private Environment config;

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create(Objects.requireNonNull(config.getProperty("var.elasticsearch.host")))
                )
        );
    }
}

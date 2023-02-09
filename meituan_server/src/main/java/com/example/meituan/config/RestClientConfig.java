package com.example.meituan.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create("http://8.218.84.229:9200")
                )
        );
    }
}

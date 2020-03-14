package com.cdugga.kafka.kafkaproducer.service;

import java.io.IOException;

import com.cdugga.kafka.kafkaproducer.data.TwitterData;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ElasticSearchService {

    private RestHighLevelClient client;

    @Autowired
    public ElasticSearchService(RestHighLevelClient client) {
        this.client = client;
    }

    public String createTweet(TwitterData data) throws IOException {

        IndexRequest indexRequest = new IndexRequest("twitter").source(data.getTweet(), XContentType.JSON);
        
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        
        log.info("ElasticSearch id {} returned", indexResponse.getId() );
        log.info("ElasticSearch info: {} ", indexResponse.getResult().toString() );
        return indexResponse.getResult().name();
    }

    

    public String createTweetWithId(TwitterData data, String tweet_id) throws IOException {

        IndexRequest indexRequest = new IndexRequest("twitter").source(data.getTweet(), XContentType.JSON).id(tweet_id);
        
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        
        log.info("ElasticSearch id {} returned", indexResponse.getId() );
        log.info("ElasticSearch info: {} ", indexResponse.getResult().toString() );
        return indexResponse.getResult().name();
    }
}
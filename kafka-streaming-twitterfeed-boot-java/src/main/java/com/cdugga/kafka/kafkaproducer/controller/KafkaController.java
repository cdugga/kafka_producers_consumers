package com.cdugga.kafka.kafkaproducer.controller;

import java.io.IOException;

import com.cdugga.kafka.kafkaproducer.data.TwitterData;
import com.cdugga.kafka.kafkaproducer.producer.Producer;
import com.cdugga.kafka.kafkaproducer.producer.TwitterProducer;
import com.cdugga.kafka.kafkaproducer.service.ElasticSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    private final Producer producer;

    private final TwitterProducer twitterProducer;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    KafkaController(Producer producer, TwitterProducer twitterProducer) {
        this.producer = producer;
        this.twitterProducer = twitterProducer;
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        this.producer.sendMessage(message);
    }

    @PostMapping(value = "/publish/twitter")
    public void sendTwitterFeedToKafkaTopic(@RequestParam("message") String message) {

        this.twitterProducer.run();
    }

    @PostMapping(value = "/insert")
    public void insertDataElasticSearch(@RequestBody String message) throws IOException {
        
        elasticSearchService.createTweet(new TwitterData(message));
    }


}

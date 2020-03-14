package com.cdugga.kafka.kafkaproducer.data;

import lombok.Data;

@Data
public class TwitterData {

    private String tweet;

    public TwitterData(String tweet){
        this.tweet = tweet;
    }
}
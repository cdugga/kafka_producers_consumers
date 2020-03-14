package com.cdugga.kafka.streams.streamprocessor.producer;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class UserJoinProducer {

    private static final Logger logger = LoggerFactory.getLogger(BankBalanceProducer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) throws InterruptedException, ExecutionException {
        logger.info(String.format("#### -> Producing message -> %s", message));
        System.out.println("User1");
        this.kafkaTemplate.send(userRecord("john", "First=Johnny,Last=Doe,Email=xyz@gmail.com")).get();
        this.kafkaTemplate.send(purchaseRecord("john", "Oranges (3)")).get();
        Thread.sleep(1000);
        System.out.println("User2");
        // this.kafkaTemplate.send(userRecord("bob", "First=Johnny,Last=Doe,Email=xyz@gmail.com")).get();
        this.kafkaTemplate.send(purchaseRecord("bob", "Avocado (3)")).get();
        Thread.sleep(1000);
        
        System.out.println("User1");
        this.kafkaTemplate.send(userRecord("john", "First=Johnny,Last=Doe,Email=xyz@gmail.com")).get();
        this.kafkaTemplate.send(purchaseRecord("john", "Oranges (10)")).get();
        Thread.sleep(1000);

        System.out.println("User3");
        this.kafkaTemplate.send(purchaseRecord("holly", "Apples (3)")).get();
        this.kafkaTemplate.send(userRecord("holly", "First=Holly,Last=Doe,Email=xyz@gmail.com")).get();
        this.kafkaTemplate.send(purchaseRecord("holly", "Blueberries (10)")).get();
        this.kafkaTemplate.send(userRecord("holly", null)).get();
        Thread.sleep(1000);
        
        System.out.println("User4");
        this.kafkaTemplate.send(userRecord("colin", "First=colin,Last=Doe,Email=xyz@gmail.com")).get();
        this.kafkaTemplate.send(purchaseRecord("colin", "Bananas (3)")).get();
        Thread.sleep(1000);
        
    }

    
    private static ProducerRecord<String, String> purchaseRecord(String key, String value){
        return new ProducerRecord<String,String>("user-purchases", key, value);
    }

    private static ProducerRecord<String, String> userRecord(String key, String value){
        return new ProducerRecord<String,String>("user-table", key, value);
    }
}
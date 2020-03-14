package com.cdugga.kafka.streams.streamprocessor.producer;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BankBalanceProducer {

    private static final Logger logger = LoggerFactory.getLogger(BankBalanceProducer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        logger.info(String.format("#### -> Producing message -> %s", message));

        int i = 0;
        while (true) {
            System.out.println("Producing batch: " + i);

            try {
                this.kafkaTemplate.send(randomTransactions("john"));
                Thread.sleep(100);
                this.kafkaTemplate.send(randomTransactions("stephane"));
                Thread.sleep(100);
                this.kafkaTemplate.send(randomTransactions("alice"));
                Thread.sleep(100);
                i += 1;
            } catch (InterruptedException x) {
                break;
            }

        }

    }

    public ProducerRecord<String, String> randomTransactions(String name) {

        ObjectNode transaction = JsonNodeFactory.instance.objectNode();
        Integer amount = ThreadLocalRandom.current().nextInt(0, 100);

        Instant now = Instant.now();
        transaction.put("name", name);
        transaction.put("amount", amount);
        transaction.put("time", now.toString());

        return new ProducerRecord<>(topic, name, transaction.toString());

    }
}
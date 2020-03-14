package com.cdugga.kafka.kafkaproducer.consumer;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.cdugga.kafka.kafkaproducer.data.TwitterData;
import com.cdugga.kafka.kafkaproducer.service.ElasticSearchService;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.Nullable;

@Log
@Service
public class Consumer implements ConsumerSeekAware{

    @Autowired
    ElasticSearchService elasticSearchService;

    @Value("${kafka.groupId}")
    private String groupId;

    @KafkaListener(topics = "${kafka.twitter.topic}" , groupId = "${kafka.groupId}", 
        containerFactory ="kafkaListenerContainerFactory")
    public void consume(List<String> message, Acknowledgment ack,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List <Integer> partition,
                        @Header(KafkaHeaders.OFFSET)List<String> offset, 
                        @Header(KafkaHeaders.GROUP_ID) List<String> groupId,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topic ,
                        @Nullable @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> key
    ) throws IOException {
       
        AtomicInteger counter = new AtomicInteger(0);
       
        if(message.size() > 0){

            message.forEach( x -> {
    
                log.info(() -> {
                    StringBuilder stringBuilder = new StringBuilder(100);
                    stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message -> %s", message.get(counter.get())));
                    stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message partition-> %s", partition.get(counter.get())));
                    stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message offset-> %s", offset.get(counter.get())));
                    stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message groupId-> %s", groupId.get(0)));
                    stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message topic-> %s", topic.get(counter.get())));
                    if(null != key){
                        stringBuilder.append(System.getProperty("line.separator")).append(String.format("#### -> Consumed message key-> %s", key.get(counter.get())));
                    }
                   
                     return stringBuilder.toString();
                } );
        
               
                TwitterData data = new TwitterData(message.get(counter.get()));
                try {
                    if(null != message.get(counter.get()) )
                    elasticSearchService.createTweetWithId(data, extractIdFromTweet(message.get(counter.get()).toString()));
                } catch (IOException | IllegalStateException | NullPointerException e) {
                    log.info("Skipping bad data:  " + e.getMessage());
                }
                counter.getAndIncrement();
    
            });
        }
        
        
       
        ack.acknowledge(); // acknowledge and allow immediate offset commit
    }


    private String extractIdFromTweet(final String argTweet) throws NullPointerException, IllegalStateException{
        JsonParser jsonParser = new JsonParser();
        JsonReader reader = new JsonReader(new StringReader(argTweet));
        reader.setLenient(true);
        return jsonParser.parse(reader).getAsJsonObject().get("id_str").getAsString();
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        // Seek all the assigned partition to a certain offset `10`
        try{

            assignments.keySet().forEach(partition -> callback.seek(groupId, partition.partition(), 1));
        }
        catch(IllegalStateException e){
            log.info("Warning : "+ e.getMessage());
        }
    }   
}
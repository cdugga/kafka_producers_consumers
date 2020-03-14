package com.cdugga.kafka.kafkaproducer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@Slf4j
@Configuration
public class KafkaStreamsConfiguration {

    @Bean
    public KafkaStreams kafkaStreams(KafkaProperties kafkaProperties,
            @Value("${spring.application.name}") String appName) {
        final Properties props = new Properties();

        // inject SSL related properties
        props.putAll(kafkaProperties.getSsl().buildProperties());
        props.putAll(kafkaProperties.getProperties());
        // stream config centric ones
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        // props.put(StreamsConfig.STATE_DIR_CONFIG, "data");
        // others
        // props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JsonNode.class);

        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamTopology(), props);
        kafkaStreams.start();

        return kafkaStreams;
    }

    private static Integer extractUserFollowersFromTweet(final String argTweet) {
        JsonParser jsonParser = new JsonParser();
        JsonReader reader = new JsonReader(new StringReader(argTweet));
        reader.setLenient(true);
        try{

            return jsonParser.parse(reader)
                            .getAsJsonObject()
                            .get("user")
                            .getAsJsonObject()
                            .get("followers_count")
                            .getAsInt();
        }
        catch(NullPointerException | IllegalStateException e ){
            log.info("Kafka stream - null or illegal state.."+e.getMessage());
            return 0;
        }
        // finally{
        //     try {
        //         reader.close();
        //     } catch (IOException e) {
        //         log.info("Some error kafka stream..."+ e.getMessage());
        //     }
        // }
    }


    @Bean
    public Topology kafkaStreamTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputTopic = streamsBuilder.stream("cdugga_batch_topic");
        KStream<String, String> filteredTweets =  inputTopic.filter(
            (k, jsonTweet) -> 
                 extractUserFollowersFromTweet(jsonTweet) > 10000
            );
        
        filteredTweets.to("important_tweets");

        return streamsBuilder.build();
    }
}
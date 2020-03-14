package com.cdugga.kafka.streams.streamprocessor;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class KafkaStreamJoinConfiguration {
    
    @Bean
    public KafkaStreams kafkaStreamsJoin(KafkaProperties kafkaProperties,
            @Value("${spring.application.name}") String appName) {
        final Properties props = new Properties();

        // inject SSL related properties
        props.putAll(kafkaProperties.getSsl().buildProperties());
        props.putAll(kafkaProperties.getProperties());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "join-application");
        // EXACTLY ONCE DELIVERY GURANTEE
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamJoinTopology(), props);

        kafkaStreams.start();
        System.out.println(kafkaStreams.toString());
        // Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }


    /**
     * Topology represents summing of users bank balances. Topic containing user name and 
     * json string containing balance information is read from Topic and stored in stream. The stream is then 
     * groupedbykey enabling aggregation using an initialBalance and helper method to calculate 
     * sum. 
     * 
     * Output is written to topic using string (name) and json string (balance)
     * 
     * @return Topology
     */
    @Bean
    public Topology kafkaStreamJoinTopology() {

        
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        GlobalKTable<String, String> userGTB = streamsBuilder.globalTable("user-table");

        KStream<String, String> userPurchases = streamsBuilder.stream("user-purchases");

        // by specifying 'join' it is implicitly stated this will be inner join and userPurchase and UserInfo will not be null 
        KStream<String, String> userPurchasesEnrichedJoin = userPurchases.join(
            userGTB,
            (key, value) -> key,
            (userPurchase, userInfo) -> "Purchases=" + userPurchase + ",UserInfo=["+userInfo+"]");

        userPurchasesEnrichedJoin.to("user-purchases-enriched-inner-join");

        KStream<String,String> userPurchasesEncrichedLeftJoin = userPurchases.leftJoin(
            userGTB,
            (key, value) -> key, 
            (userPurchase, userInfo) -> {
                if(userInfo !=null){
                    return "Purchase=" + userPurchase + ",UserInfo=[" + userInfo+ "]";
                }
                else{
                    return "Purchase=" + userPurchase + ",UserInfo=null"; 
                }
            });


        userPurchasesEncrichedLeftJoin.to("user-purchases-enriched-left-join");

        return streamsBuilder.build();
    }

}
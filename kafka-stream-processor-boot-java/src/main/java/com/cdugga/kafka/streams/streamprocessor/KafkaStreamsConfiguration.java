package com.cdugga.kafka.streams.streamprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class KafkaStreamsConfiguration {

    @Bean
    public KafkaStreams kafkaStreams(KafkaProperties kafkaProperties,
            @Value("${spring.application.name}") String appName) {
        final Properties props = new Properties();

        // inject SSL related properties
        props.putAll(kafkaProperties.getSsl().buildProperties());
        props.putAll(kafkaProperties.getProperties());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        // EXACTLY ONCE DELIVERY GURANTEE
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamBankBalancesTopology(), props);

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
    public Topology kafkaStreamBankBalancesTopology() {

        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, JsonNode> transactions = streamsBuilder.stream("bank-transactions",
                Consumed.with(Serdes.String(), jsonSerde));

        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

        // Serialized.with(Serdes.String(), jsonSerde)
        KTable<String, JsonNode> bankBalance = transactions.groupByKey(Grouped.with(Serdes.String(), jsonSerde))
                .aggregate(() -> initialBalance, (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as("bank-balance-agg")
                                .withKeySerde(Serdes.String()).withValueSerde(jsonSerde));

        bankBalance.toStream().to("bank-balances-exactly-once", Produced.with(Serdes.String(), jsonSerde));

        return streamsBuilder.build();
    }

    private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());

        Long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
        Long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        newBalance.put("time", newBalanceInstant.toString());

        return newBalance;
    }

    /**
     * Topology reads a stream of user favorite colors simply separated by comma, no keys. A new stream is
     * created using user as key and color as value. This stream is written to a tempoary topic. The topic is then 
     * read into a ktable where records are grouped by color. The table represents color and its count. This KTable
     * is converted to a stream and written back to a Kafka topic
     * 
     * @return Topology
     */
    @Bean
    public Topology kafkaStreamFavoriteColorTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> textLines = streamsBuilder.stream("favorite-color-input1");

        KStream<String, String> usersAndColors = textLines.filter((key, value) -> value.contains(","))
                .selectKey((key, value) -> value.split(",")[0].toLowerCase())
                .mapValues((value) -> value.split(",")[1].toLowerCase())
                .filter((user, color) -> Arrays.asList("green", "red", "blue").contains(color));

        usersAndColors.to("user-keys-and-colors1");

        KTable<String, String> userColorTable = streamsBuilder.table("user-keys-and-colors1");

        KTable<String, Long> favoriteColor = userColorTable.groupBy((user, color) -> new KeyValue<>(color, color))
                .count(Materialized.as("CountsByColor"));

        favoriteColor.toStream().to("favorite-color-output1", Produced.with(Serdes.String(), Serdes.Long()));

        return streamsBuilder.build();
    }

    /**
     * Toplology for getting work count. Uses groupByKey (key being word) to sum occurences of a given word
     * The result is written to Kafka stream with @String key representing the work and @Long value representing
     * the count
     * @return @Toplogy
     */
    @Bean
    public Topology kafkaStreamTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> wordCountInput = streamsBuilder.stream("word-count-input");

        KTable<String, Long> wordCounts = wordCountInput.mapValues(value -> value.toLowerCase())
                .flatMapValues(lowercase -> Arrays.asList(lowercase.split(" "))).selectKey((key, word) -> word)
                .groupByKey().count(Materialized.as("Counts"));

        wordCounts.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

        return streamsBuilder.build();
    }

}
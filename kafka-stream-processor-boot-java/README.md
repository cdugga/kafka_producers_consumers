# kafka-stream-processor

#### Running instructions

Create consumer
```
~/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic word-count-output --from-beginning --property print.
key=true --property key.value=true, formatter kafka.tools.DefaultMessageFormatter --property key.deserializer=org.apache.kafka.common.serialization.StringDeseria
lizer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

Create producer
```
~/kafka_2.12-2.3.1# kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic word-count-input
```

Create consumer for favorites
```
./bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic favorite-color-output1 --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

Create consumer for summed bank balances (boot app acts as producer , pushing random balance information to Kafka topic once server has started)
```
 ./bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic bank-balances-exactly-once --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.connect.json.JsonDeserializer
```

Topic to hold eventual balances should use compact cleanup policy
kafka-topics.sh --bootstrap-server localhost:9092 --topic bank-balances-exactly-once --create --partitions 3 --replication-factor 1 --config cleanup.policy=compact


Start SpringBoot application

# Bike Share station data

## Description
Kafka producer application which streams data from bikeshare open API and pushes to Kafka topic. 
***
### Tech
- Kakfa Producer API
- Java 8
- Webclient API
- Maven 
***
### Running instructions

#### Create Kafka Topic

Temper number of partitions and replication factor based on number of brokers in cluster (example below is for single broker execution)

```bash
kafka-topics.sh --bootstrap-server localhost:9092 --topic bikeshare-station-data --create --partitions 1 --replication-factor 1
```

***
#### Running SpringBoot application

Build project

```maven 
mvn clean package 
```

Run boot application

```java
java -jar ./target/station-opendata-streaming<version>.jar
```
***
#### Creating a Kafka console listener to sanity check ingested data

```bash
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic bikeshare-station-data --from-beginning \
--property print. key=true --property key.value=true, formatter kafka.tools.DefaultMessageFormatter \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```


##### Start Zookeeper and Kafka servers ######

##### Starting Kafka server ######
#1. create C:\kafka_2.12-2.3.1\data\kafka
#2. update config/server.properties -> set log.dirs=C:\kafka_2.12-2.3.1\data\kafka
# or for Linux /mnt/c/kafka_2.12-2.3.1/data/kafka

#Windows
kafka-server-start.bat C/kafka_2.12-2.3.1/config/server.properties
#Linux
kafka-server-start.sh /root/kafka_2.12-2.3.1/config/server.properties
or 
./bin/kafka-server-start.sh ./config/server.properties

##### Starting Zookeeper server ######
#1. create zookeeper data folder in C:\kafka_2.12-2.3.1\data\zookeeper
#2. update zookeeper.properties in C:\kafka_2.12-2.3.1\config 
# or for Linux /mnt/c/kafka_2.12-2.3.1/data/zookeeper

#Windows
zookeeper-server-start.bat dataDir=kafka_2.12-2.3.1/config/zookeeper.properties

#Linux
zookeeper-server-start.sh dataDir=/root/kafka_2.12-2.3.1/config/zookeeper.properties
or
./bin/zookeeper-server-start.sh ./config/zookeeper.properties

######### CLI commands #################

#1. Create a topic 
kafka-topics.bat --bootstrap-server localhost:9092 --topic cdugga_topic --create --partitions 1 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9092 --topic cdugga_topic --create --partitions 1 --replication-factor 1


#2 List all topics
kafka-topics.bat --zookeeper 127.0.0.1:2181 --list
kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

#3 Describe a particular topic
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic cdugga_topic --describe

#4 Delete topic (will mark for deletion - requires delete.topic.enable set to true)
kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic cdugga_topic --delete

#5 create producer
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic cdugga_topic

#6 create producer with ACKS
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic cdugga_topic --producer-property acks=all

#7 Kafka consumer (read stream)
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic cdugga_topic

#8 Kafka consumer (read from beginning)
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic cdugga_topic --from-beginning

#9 Kafka consumer groups
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic cdugga_topic --group cdugga-cgroup

#10 view consumer groups
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --list

#11 Describe cgroups
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group cdugga-cgroup

#12 Reset consumer group offset
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group cdugga_twitter_group --reset-offsets --to-earliest --execute --topic cdugga_topic

#13 Shift-by consumer group in either direction 
kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group cdugga-cgroup --reset-offsets --shift-by -2 --execute --topic cdugga_topic

#14 Use producer to send key/value pairs
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic cdugga_topic  --property parse.key=true --property key.separator=,

#15 Consumer consume key/value pairs
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic cdugga-topic --from-beginning --property print.key=true --property key.separator=,

#16 Describe config associated with an entity in Kafka
kafka-configs.sh --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name configured-topic --describe

#17 Add config
kafka-configs.sh --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name configured-topic --add-config min.insync.replicas=2 --alter

#18 Delete config
kafka-configs.sh --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name configured-topic --delete-config min.insync.replicas --alter


#19 Create topic with log compaction
kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic compactTopic --partitions 1 --replication-factor 1 --config cleanup.policy=compact --config
min.cleanable.dirty.ratio=0.001 --config segment.ms=5000

#20 Use consumer to also print key associated with value
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic compactTopic --from-beginning --property print.key=true --property key.separator=

#21 Set min.insync.replicas
kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic syncTopic --partitions 3 --replication-factor 1



#21
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic word-count-output --from-beginning --property print.
key=true --property key.value=true, formatter kafka.tools.DefaultMessageFormatter --property key.deserializer=org.apache.kafka.common.serialization.StringDeseria
lizer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

kafka-configs.sh --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name syncTopics --add-config min.insync.replicas=2 --alter

#22 Working with Kafka connect /connectors
Download Kafka (pre-requisite). Navigate to KAFKA_HOME/config. Update connect-distributed.properties. 
Set boostrap server to correspond to the Kafka server you have already started (see command above for bootstrap server address)
Update rest port (make sure unused)
Update plugins path (something of your choice)
Add connector of your choice (download from confluenthub or wherever your desired plugin is available)
Unzip in plugins directory configured in previous steps. 




####
Providing a key will ensure key is always written to same partition

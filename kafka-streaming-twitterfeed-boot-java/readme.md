# Kafka Producer/Consumer application

SpringBoot application which produces and consumes messages to Kafka topic

## Getting Started

Clone project from github.com
	
### Prerequisites

What things you need to install the software and how to install them
```
kafka running locally on port 9092
Zookeeper
JDK 8
Maven
```

### Running a single instance

Run SpringBoot applcation from your IDE (STS , VSCode etc)

Alternatively run from command line

```
 /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -cp /tmp/cp_3h6zxraxsfw9yx2i1e0e8q9qq.jar com.cdugga.kafka.kafkaproducer.KafkaProducerApplication
 
```

### Running multiple instance (to simulate a consumer group)

Create additional application.properties and application.yaml files to describe consumer behavior of each consumer you want in the consumer group.

Use SpringBoot profiles to specify active profile at startup. (Remember to update port number if using same OS)

```
 /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -Dspring.profiles.active=con1 -cp /tmp/cp_3h6zxraxsfw9yx2i1e0e8q9qq.jar com.cdugga.kafka.kafkaproducer.KafkaProducerApplication
 
```


### Initiate producer message sending 

Once SpringBoot app is running use REST API to initiate producer message

Say what the step will be

```
curl -X POST -F 'message=somemessage' http://localhost:9000/kafka/publish

curl -X POST -F 'message=somemessage' http://localhost:9000/kafka/publish/twitter

curl -X POST -H "Content-Type: application/json" -F 'message=somemessage' --data "{\"foo\":\"bar\"}" http://localhost:9000/kafka/insert
```

### Troubleshooting issues with Twitter API and  expires token
On Linux system (or WSL)

```
ntpdate ntp.org
```
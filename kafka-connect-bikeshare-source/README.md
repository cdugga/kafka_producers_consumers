# Kafka Source Connector for _bikeshare data_ Ireland

## Running the source connector
* Assumes Kafka cluster/Zookeeper already running
* Package connector by running mvn clean package
* Copy ./config/BikeShareConnector.properties to %{KAFKA_HOME}/config
* Copy jar files generated in second step to ${KAFKA_HOME}/plugins (or whatever directory you choose to place Kafka connectors)
* Create target topic if not already done
* Run following command to begin connector in standalone mode (single process)
` ./bin/connect-standalone.sh config/worker.properties config/BikeShareConnector.properties` 

  

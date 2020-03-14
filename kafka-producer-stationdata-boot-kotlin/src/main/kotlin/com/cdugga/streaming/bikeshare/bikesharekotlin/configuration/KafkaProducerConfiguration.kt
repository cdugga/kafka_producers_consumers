package com.cdugga.streaming.bikeshare.bikesharekotlin.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.KafkaTemplate
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.context.annotation.Bean
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory

@EnableKafka
@Configuration
open class KafkaProducerConfiguration{
	
	@Autowired
	lateinit var kafkaProperties:KafkaProperties
	
	
	@Bean
    open fun kafkaTemplateCustom():KafkaTemplate<String, String> {
      return KafkaTemplate(producerFactory())
    }

    @Bean
    open fun producerFactory():ProducerFactory<String, String> {
      val configProps = HashMap(kafkaProperties.buildProducerProperties())
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
      configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
      configProps.put(ProducerConfig.ACKS_CONFIG, "all")
      configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5")
      configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
      configProps.put(ProducerConfig.LINGER_MS_CONFIG, "20")
      configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(23 * 1024))
      return DefaultKafkaProducerFactory(configProps)
    }
	
}
package com.cdugga.streaming.bikeshare.bikesharekotlin.producer

import org.springframework.beans.factory.annotation.Value

import org.springframework.kafka.core.KafkaTemplate
import com.cdugga.streaming.bikeshare.bikesharekotlin.Data
import org.springframework.stereotype.Service
import com.cdugga.streaming.bikeshare.bikesharekotlin.StationData
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClient;
import org.apache.kafka.clients.producer.ProducerRecord
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.http.MediaType
import java.time.Duration
import java.util.Arrays
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory

/**
 *
 * Created by @cdugga
 **/

@Service

class StationDataProducer(val kafkaTemplate: KafkaTemplate<String, String>) {

	@Value("\${kafka.topic}")
	lateinit var topic: String

	@Value("\${open.data.api.url}")
	lateinit var url: String;

	@Value("\${open.data.api.key}")
	lateinit var key: String;

	@Value("\${open.data.api.schemeId}")
	lateinit var schemeId: String;


	val logger = LoggerFactory.getLogger(StationDataProducer::class.java)

	fun sendMessage(message: Data) {
		var list = message.data.toList();
		
		list.forEach{ send(it) }
	}

	fun send(message: StationData) {
		println("message.." + message)
		this.kafkaTemplate.send(stationDataString(message))
	}

	fun getStationDataNonBlocking(): Flux<Data> {
		println("Entering NON-BLOCKING Controller!");
		var stringFlux: Flux<Data> = WebClient.create()
			.post()
			.uri(url + "?schemeId=" + schemeId + "&key=" + key)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.bodyToFlux(Data::class.java)
			.repeatWhen({ longFlux -> Flux.interval(Duration.ofSeconds(5)) })
			.retry()
			.onErrorContinue({ _, o -> logger.info("Request Error - value ignored ()", o) })
			.log("Repeating request");


		stringFlux.subscribe(this::sendMessage)
		println("Exiting NON-BLOCKING Controller!");
		return stringFlux;
	}

	fun getObjectNode(): ObjectNode = JsonNodeFactory.instance.objectNode();

	fun createProducerRecord(data: StationData, json: ObjectNode): ProducerRecord<String, String> =
		ProducerRecord<String, String>("station-open-data-topic", data.name, json.toString());

	fun stationDataString(data: StationData): ProducerRecord<String, String> {
		var stationData: ObjectNode = getObjectNode()
		stationData.put("name", data.name);
		stationData.put("irishName", data.nameIrish);
		stationData.put("docksCount", data.docksCount);
		stationData.put("bikesAvailable", data.bikesAvailable);
		stationData.put("docksAvailable", data.docksAvailable);
		stationData.put("date", data.dateStatus);
		stationData.put("schemeLocation", data.schemeShortName);
		return createProducerRecord(data, stationData);
	}

}
package com.cdugga.streaming.stationopendatastreaming.producer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.reactive.function.client.WebClient;

import com.cdugga.streaming.stationopendatastreaming.Data;
import com.cdugga.streaming.stationopendatastreaming.StationData;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class StationDataProducer {

	@Value("${kafka.topic}")
    private String topic;
	
	@Value("${open.data.api.url}")
	private String url;
	
	@Value("${open.data.api.key}")
	private String key;
	
	@Value("${open.data.api.schemeId}")
	private String schemeId;
	
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
	
	public Flux<Data> getStationDataNonBlocking() {
		log.info("Entering NON-BLOCKING Controller!");
		
		Flux<Data> stringFlux = WebClient.create()
				.post()
				.uri(url + "?schemeId="+ schemeId +"&key="+ key)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.retrieve()
				.bodyToFlux(Data.class)
				.repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(60)))
				.retry()
				.onErrorContinue((throwable,o) -> log.info("Request Error - value ignored ()", o))
				.log("Repeating request");
		
		stringFlux.subscribe(this::sendMessage);
		
		log.info("Exiting NON-BLOCKING Controller!");
		return stringFlux;
	}

	public void sendMessage(Data message) {
		List<StationData> list = Arrays.asList(message.getData());
		list.forEach(this::send);
	}
	
	public void send(final StationData stationDataMsg) {
		ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(stationDataString(stationDataMsg));
		
		future.addCallback( success -> {
			log.info("Publish message succeeded, offset" +success.getRecordMetadata().offset());
		}, failure -> {
			log.info("Publishing message failed.."+ failure.getMessage());
		});
			
			
	}
    
    public static ProducerRecord<String, String> stationDataString(final StationData data){
    	
    	ObjectNode stationData = JsonNodeFactory.instance.objectNode();
    	
    	stationData.put("name", data.getName());
    	stationData.put("irishName", data.getNameIrish());
    	stationData.put("docksCount", data.getDocksCount() );
    	stationData.put("bikesAvailable", data.getBikesAvailable());
    	stationData.put("docksAvailable", data.getDocksAvailable());
    	stationData.put("date", data.getDateStatus());
    	stationData.put("schemeLocation", data.getSchemeShortName());
    	
    	return new ProducerRecord<String, String>("bikeshare-station-data", data.getName(), stationData.toString());
    }
}		

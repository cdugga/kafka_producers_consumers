package com.cdugga.opendata.lambda.stationdatalambdajava.functions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KafkaProducerFunction implements Function<String, String> {

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

	@Override
	public String apply(String t) {
		getStationDataNonBlocking();
		return "Producer pushing data to Kafka Topic";
	}

	public void getStationDataNonBlocking() {
		System.out.println("Entering Lambda Controller!");
	    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    
	    MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	    map.add("schemeId", schemeId);
	    map.add("key", key);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		RestTemplate restemplate = new RestTemplate();
		Data response = restemplate.postForObject(url, request, Data.class);
		
		sendMessage(response);
	}

	public void sendMessage(Data message) {
		List<StationData> list = Arrays.asList(message.getData());
		list.forEach(this::send);
	}

	public void send(final StationData stationDataMsg) {
		this.kafkaTemplate.send(stationDataString(stationDataMsg));
	}

	public static ProducerRecord<String, String> stationDataString(final StationData data) {

		ObjectNode stationData = JsonNodeFactory.instance.objectNode();

		stationData.put("name", data.getName());
		stationData.put("irishName", data.getNameIrish());
		stationData.put("docksCount", data.getDocksCount());
		stationData.put("bikesAvailable", data.getBikesAvailable());
		stationData.put("docksAvailable", data.getDocksAvailable());
		stationData.put("date", data.getDateStatus());
		stationData.put("schemeLocation", data.getSchemeShortName());

		return new ProducerRecord<String, String>("station-open-data-topic", data.getName(), stationData.toString());
	}

}

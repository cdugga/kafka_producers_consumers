package com.cdugga.streaming.stationopendatastreaming;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.cdugga.streaming.stationopendatastreaming.producer.StationDataProducer;

/**
 * 
 * @author cdugga
 *
 */
@SpringBootApplication
public class StationOpendataStreamingApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(StationOpendataStreamingApplication.class, args);
		
		StationDataProducer stationDataProducer = applicationContext.getBean(StationDataProducer.class);

		stationDataProducer.getStationDataNonBlocking();
	}

}

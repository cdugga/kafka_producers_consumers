package com.cdugga.streaming.bikeshare.bikesharekotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import com.cdugga.streaming.bikeshare.bikesharekotlin.producer.StationDataProducer

@SpringBootApplication
open class BikeshareKotlinApplication

fun main(args: Array<String>) {
	val application:ConfigurableApplicationContext = runApplication<BikeshareKotlinApplication>(*args)
	
	var producer:StationDataProducer = application.getBean(StationDataProducer::class.java)
	
	producer.getStationDataNonBlocking()
}

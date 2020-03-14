package com.cdugga.opendata.lambda.stationdatalambdajava.functions;

@lombok.Data
public class Data {

	private int responseCode;
	
	private String responseText;
	
	private String responseDate;
	
	private StationData [] data;
}

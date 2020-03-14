package com.cdugga.opendata.lambda.stationdatalambdajava.functions;

import lombok.Data;

@Data
public class StationData {

	private int schemeId;
	
	private String schemeShortName;
	
	private int stationId;
	
	private String name;
	
	private String nameIrish;
	
	private int docksCount;
	
	private int bikesAvailable;
	
	private int docksAvailable;
	
	private int status;
	
	private double lattitude;
	
	private double longitude;
	
	private String dateStatus;
	
}

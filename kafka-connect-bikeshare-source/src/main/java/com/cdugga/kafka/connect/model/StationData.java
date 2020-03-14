package com.cdugga.kafka.connect.model;

import com.cdugga.kafka.connect.BikeShareStationDataSchema;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class StationData {

	private static final Logger log = LoggerFactory.getLogger(StationData.class);

	private int schemeId;
	
	private String schemeShortName;
	
	private int stationId;
	
	private String name;
	
	private String nameIrish;
	
	private int docksCount;
	
	private int bikesAvailable;
	
	private int docksAvailable;
	
	private int status;
	
	private int lattitude;
	
	private int longitude;
	
    private String dateStatus;
    
    public int getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(int schemeId) {
		this.schemeId = schemeId;
	}

	public String getSchemeShortName() {
		return schemeShortName;
	}

	public void setSchemeShortName(String schemeShortName) {
		this.schemeShortName = schemeShortName;
	}

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameIrish() {
		return nameIrish;
	}

	public void setNameIrish(String nameIrish) {
		this.nameIrish = nameIrish;
	}

	public int getDocksCount() {
		return docksCount;
	}

	public void setDocksCount(int docksCount) {
		this.docksCount = docksCount;
	}

	public int getBikesAvailable() {
		return bikesAvailable;
	}

	public void setBikesAvailable(int bikesAvailable) {
		this.bikesAvailable = bikesAvailable;
	}

	public int getDocksAvailable() {
		return docksAvailable;
	}

	public void setDocksAvailable(int docksAvailable) {
		this.docksAvailable = docksAvailable;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getLattitude() {
		return lattitude;
	}

	public void setLattitude(int lattitude) {
		this.lattitude = lattitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public String getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(String dateStatus) {
		this.dateStatus = dateStatus;
    }   
    
    public static StationData fromJson(JSONObject jsonObject){
		log.info(jsonObject.toString());
		StationData data = new StationData();
		
		
        data.setBikesAvailable(jsonObject.getInt(BikeShareStationDataSchema.BIKES_AVAILABLE));
        data.setDateStatus(jsonObject.getString(BikeShareStationDataSchema.DATE_STATUS));
        data.setDocksAvailable(jsonObject.getInt(BikeShareStationDataSchema.DOCKS_AVAILABLE));
        data.setDocksCount(jsonObject.getInt(BikeShareStationDataSchema.DOCKS_COUNT));
        data.setLattitude(jsonObject.getInt(BikeShareStationDataSchema.LATTITUDE));
        data.setLongitude(jsonObject.getInt(BikeShareStationDataSchema.LONGITUDE));
        data.setName(jsonObject.getString(BikeShareStationDataSchema.NAME));
        data.setNameIrish(jsonObject.getString(BikeShareStationDataSchema.NAME_IRISH));
        data.setSchemeId(jsonObject.getInt(BikeShareStationDataSchema.SCHEME_ID));
        data.setSchemeShortName(jsonObject.getString(BikeShareStationDataSchema.SCHEME_SHORT_NAME));
        data.setStationId(jsonObject.getInt(BikeShareStationDataSchema.SCHEME_ID));
        data.setStatus(jsonObject.getInt(BikeShareStationDataSchema.STATUS));

        
        return data;
    }
	
}
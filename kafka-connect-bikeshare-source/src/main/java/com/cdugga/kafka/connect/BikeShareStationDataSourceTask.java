package com.cdugga.kafka.connect;

import com.cdugga.kafka.connect.model.StationData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






import static com.cdugga.kafka.connect.BikeShareStationDataSchema.*;

/**
 * Created by @cdugga
 */

public class BikeShareStationDataSourceTask extends SourceTask {
  /*
   * Your connector should never use System.out for logging. All of your classes
   * should use slf4j for logging
   */
  static final Logger log = LoggerFactory.getLogger(BikeShareStationDataSourceTask.class);

  BikeShareAPIHttpClient bikeShareHttpAPIClient;

  public BikeShareStationDataConnectorConfig config;

  protected String schemeID;

  protected String apiKey;

  protected String topic;

  private CountDownLatch stopLatch = new CountDownLatch(1);

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    config = new BikeShareStationDataConnectorConfig(map);
    schemeID = config.getApiKey();
    apiKey = config.getApiKey();
    topic = config.getTopic();
    bikeShareHttpAPIClient = new BikeShareAPIHttpClient(config);
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {

    log.info("Entering poll method****************");
    final ArrayList<SourceRecord> records = new ArrayList<>();
    stopLatch.await(2, TimeUnit.SECONDS);

    JSONArray stationData = bikeShareHttpAPIClient.getStationData();
    for (Object obj : stationData) {

      log.info("jsonobj.....", obj.toString());
      JSONObject temp = (JSONObject)obj;
      JSONArray array = temp.getJSONArray("data");
     
      for(Object dataObj: array){
        StationData data = StationData.fromJson((JSONObject)dataObj);
        SourceRecord sourceRecord = generateSourceRecord(data);
        records.add(sourceRecord);
      }
    }
    log.info("EXIT POLL", records.size());
    return records;

  }

  private SourceRecord generateSourceRecord(StationData stationData) {
    Instant instant =  Instant.now();
    // return new SourceRecord(null, null, topic, null,/* KEY_SCHEMA, buildRecordKey(stationData),*/ STATION_DATA_SCHEMA,
    //     buildRecordValue(stationData) /*, instant.toEpochMilli()*/);

        return new SourceRecord(sourcePartition(), null, topic, null, KEY_SCHEMA, 
        buildRecordKey(stationData), STATION_DATA_SCHEMA,
        buildRecordValue(stationData) , instant.toEpochMilli());

  }

    private Map<String, String> sourcePartition() {
      Map<String, String> map = new HashMap<>();
      map.put(SCHEME_ID, config.getSchemeId());
      map.put(SCHEME_ID, config.getSchemeId());
      return map;
  }

  public Struct buildRecordValue(StationData data) {
    Struct valueStruct = new Struct(STATION_DATA_SCHEMA)
        .put(BIKES_AVAILABLE, data.getBikesAvailable())
        .put(DATE_STATUS, data.getDateStatus())
        .put(DOCKS_AVAILABLE, data.getDocksAvailable())
        .put(DOCKS_COUNT, data.getDocksCount())
        .put(LATTITUDE, data.getLattitude())
        .put(LONGITUDE, data.getLongitude())
        .put(NAME, data.getName())
        .put(NAME_IRISH, data.getNameIrish())
        .put(SCHEME_ID, data.getSchemeId())
        .put(SCHEME_SHORT_NAME, data.getSchemeShortName())
        .put(STATION_ID, data.getStationId())
        .put(STATUS, data.getStatus());
     
    return valueStruct;
  }

  private Struct buildRecordKey(StationData data) {
    // Key Schema
    Struct key = new Struct(KEY_SCHEMA).put(SCHEME_ID, data.getSchemeId())
        .put(SCHEME_SHORT_NAME, data.getSchemeShortName()).put(STATION_ID, data.getStationId());

    return key;
  }

  @Override
  public void stop() {
  }
}
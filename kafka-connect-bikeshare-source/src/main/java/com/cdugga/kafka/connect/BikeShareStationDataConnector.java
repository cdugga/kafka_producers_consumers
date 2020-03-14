package com.cdugga.kafka.connect;

import com.cdugga.kafka.connect.VersionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by @cdugga
 */
public class BikeShareStationDataConnector extends SourceConnector {
  /*
   * Your connector should never use System.out for logging. All of your classes
   * should use slf4j for logging
   */
  private static Logger log = LoggerFactory.getLogger(BikeShareStationDataConnector.class);
  private BikeShareStationDataConnectorConfig config;

  BikeShareAPIHttpClient bikeShareAPIHttpClient;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    config = new BikeShareStationDataConnectorConfig(map);

    // // initializeLastVariables();
    // bikeShareAPIHttpClient = new BikeShareAPIHttpClient(config);
  }

  @Override
  public Class<? extends Task> taskClass() {
    return BikeShareStationDataSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    ArrayList<Map<String, String>> configs = new ArrayList<>(1);
    configs.add(config.originalsStrings());
    return configs;
  }

  @Override
  public void stop() {
    // not required in this scenario
  }

  @Override
  public ConfigDef config() {
    return BikeShareStationDataConnectorConfig.config();
  }

}

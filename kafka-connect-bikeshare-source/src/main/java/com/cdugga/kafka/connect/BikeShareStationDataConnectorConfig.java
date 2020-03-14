package com.cdugga.kafka.connect;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

/**
 * @Created by @cdugga
 */
public class BikeShareStationDataConnectorConfig extends AbstractConfig {

  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_DOC = "Topic to write to";

  public static final String API_KEY = "bikeshare.data.api.key";
  private static final String API_KEY_DOC = "Api key for fetching data from bikeshare api";

  public static final String SCHEMD_ID = "bikeshare.data.schemeId";
  private static final String SCHEMD_ID_DOC = "Scheme to fetch data from E.g. 4=Galway, 1=All";


  public BikeShareStationDataConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public BikeShareStationDataConnectorConfig(Map<String, String> originals) {
    this(config(), originals);
  }

  public static ConfigDef config() {
    return new ConfigDef()
    .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
    .define(API_KEY, Type.STRING, Importance.HIGH, API_KEY_DOC)
    .define(SCHEMD_ID, Type.STRING, Importance.HIGH, SCHEMD_ID_DOC);
  }

  public String getTopic() {
    return this.getString(TOPIC_CONFIG);
  }

  public String getApiKey() {
    return this.getString(API_KEY);
  }

  public String getSchemeId() {
    return this.getString(SCHEMD_ID);
  }



}

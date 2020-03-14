package com.cdugga.kafka.connect;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

/**
 * created by @cdugga
 */
public class BikeShareStationDataSchema {

    // ancillary fields
    public static final int responseCode = 0;
    public static final String responseText = "request status";
    public static final String responseDate = "date string";

    // station data fields
    public static final String SCHEME_ID = "schemeId";

    public static final String SCHEME_SHORT_NAME = "schemeShortName";

    public static final String STATION_ID = "stationId";

    public static final String NAME = "name";

    public static final String NAME_IRISH = "nameIrish";

    public static final String DOCKS_COUNT = "docksCount";

    public static final String BIKES_AVAILABLE = "bikesAvailable";

    public static final String DOCKS_AVAILABLE = "docksAvailable";

    public static final String STATUS = "status";

    public static final String LATTITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String DATE_STATUS = "dateStatus";

    public static final String SCHEMA_VALUE_STATIONDATA = "data";

    public static final String SCHEMA_KEY = "com.cdugga.kafka.connect.bikeshare.Key";

    public static final Schema STATION_DATA_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_STATIONDATA).version(1)
            .field(SCHEME_ID, Schema.INT32_SCHEMA).field(SCHEME_SHORT_NAME, Schema.STRING_SCHEMA)
            .field(STATION_ID, Schema.INT32_SCHEMA).field(NAME, Schema.STRING_SCHEMA)
            .field(NAME_IRISH, Schema.STRING_SCHEMA).field(DOCKS_COUNT, Schema.INT32_SCHEMA)
            .field(BIKES_AVAILABLE, Schema.INT32_SCHEMA).field(DOCKS_AVAILABLE, Schema.INT32_SCHEMA)
            .field(STATUS, Schema.INT32_SCHEMA).field(LATTITUDE, Schema.OPTIONAL_INT32_SCHEMA)
            .field(LONGITUDE, Schema.OPTIONAL_INT32_SCHEMA).field(DATE_STATUS, Schema.STRING_SCHEMA).build();

    // Key Schema
    public static final Schema KEY_SCHEMA = SchemaBuilder.struct().name(SCHEMA_KEY).version(1)
            .field(SCHEME_ID, Schema.INT32_SCHEMA).field(STATION_ID, Schema.INT32_SCHEMA)
            .field(SCHEME_SHORT_NAME, Schema.STRING_SCHEMA).build();

}
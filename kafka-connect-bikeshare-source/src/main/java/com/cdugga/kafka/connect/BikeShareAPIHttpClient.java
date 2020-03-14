package com.cdugga.kafka.connect;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by @cdugga
 */
public class BikeShareAPIHttpClient {

    private static final Logger log = LoggerFactory.getLogger(BikeShareAPIHttpClient.class);

    BikeShareStationDataConnectorConfig config;

    public BikeShareAPIHttpClient(BikeShareStationDataConnectorConfig config) {
        this.config = config;
    }

    protected JSONArray getStationData() throws InterruptedException {
        log.info("Get station data ");
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = getLiveStationData();

            switch (jsonResponse.getStatus()) {
            case 200:
                log.info("Request successful 200...returning jsonresponse;");
                log.info(jsonResponse.getBody().getArray().toString());
                return jsonResponse.getBody().getArray();
            case 403:
                log.info("Returned 403...throttling next request;");
                Thread.sleep(3000);
                return getStationData();
            default:
                Thread.sleep(5000L);
                return getStationData();
            }

        } catch (UnirestException e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return new JSONArray();

        }
    }

    protected HttpResponse<JsonNode> getLiveStationData() throws UnirestException {
        log.info("Get Live station data ");
        String bikeShareUrl = "https://data.bikeshare.ie/dataapi/resources/station/data/list?";
        String bikeShareParams = "schemeId=" + config.getSchemeId() + "&key=" + config.getApiKey();

        Unirest.setTimeouts(0, 0);
        HttpResponse<JsonNode> response = null;
        if (!config.getApiKey().isEmpty() && !config.getSchemeId().isEmpty()) {
            log.info("Api Key and SchemeId present.. Making request.");
            response = Unirest.post(bikeShareUrl + bikeShareParams)
                    .header("Content-Type", "application/x-www-form-urlencoded").asJson();

        }
        log.info("RESPONSE...", response);
        return response;
    }

}
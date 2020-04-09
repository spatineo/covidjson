package com.spatineo.covidjson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;

/**
 * Simple conversion tool for producing sample CovidJSON files from coronavirus data fetched from the HS API
 * @author Ilkka Rinne / Spatineo 2020
 * */
@SpringBootApplication
public class HSAPIToCovidJSONConverter implements CommandLineRunner {
    private static final Logger LOGGER = Logger.getLogger(HSAPIToCovidJSONConverter.class.getName());

    private static final Map<String, String> HS_DISTRICT_ID_TO_CANONICAL_IDS;
    private static final Map<String, String> DISTRICT_NAMES;
    private static final String API_URL= "https://w3qa5ydb4l.execute-api.eu-west-1.amazonaws.com/prod/processedThlData";

    private static Map<String, List<ZonedDateTime>> datesByDistrict = new HashMap<>();
    private static Map<String, List<Integer>> confirmedCasesByDistrict = new HashMap<>();
    private static Map<String, Geometry> geometryCache = new HashMap<>();
    private static Map<String, String> templateCache = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();
    private static ZonedDateTime startDate = ZonedDateTime.of(LocalDateTime.of(2020, 2, 24, 0, 0, 0), ZoneId.of("Europe/Helsinki"));
    private static File outputPath = new File("./out");
    private static File cachePath = new File("./.cache");
    private static int cacheMaxAgeHours = -1;

    static {
        HashMap<String, String> m = new HashMap<>();
        m.put("finland/health-care-districts/ahvenanmaa", "Åland");
        m.put("finland/health-care-districts/etela-karjala", "South Karelia Hospital District");
        m.put("finland/health-care-districts/etela-pohjanmaa", "South Ostrobothnia Hospital District");
        m.put("finland/health-care-districts/etela-savo", "Etelä-Savo Hospital District");
        m.put("finland/health-care-districts/hus", "Helsinki and Uusimaa Hospital District");
        m.put("finland/health-care-districts/ita-savo", "Itä-Savo Hospital District");
        m.put("finland/health-care-districts/kainuu", "Kainuu Hospital District");
        m.put("finland/health-care-districts/kanta-hame", "Kanta-Häme Hospital District");
        m.put("finland/health-care-districts/keski-pohjanmaa", "Central Ostrobothnia Hospital District");
        m.put("finland/health-care-districts/keski-suomi", "Central Finland Hospital District");
        m.put("finland/health-care-districts/kymenlaakso", "Kymenlaakso Hospital District");
        m.put("finland/health-care-districts/lansi-pohja", "Länsi-Pohja Hospital District");
        m.put("finland/health-care-districts/lappi", "Lappi Hospital District");
        m.put("finland/health-care-districts/paijat-hame", "Päijät-Häme Hospital District");
        m.put("finland/health-care-districts/pirkanmaa", "Pirkanmaa Hospital District");
        m.put("finland/health-care-districts/pohjois-karjala", "North Karelia Hospital District");
        m.put("finland/health-care-districts/pohjois-pohjanmaa", "North Ostrobothnia Hospital District");
        m.put("finland/health-care-districts/pohjois-savo", "Pohjois-Savo Hospital District");
        m.put("finland/health-care-districts/satakunta", "Satakunta Hospital District");
        m.put("finland/health-care-districts/vaasa", "Vaasa Hospital District");
        m.put("finland/health-care-districts/varsinais-suomi", "Varsinais-Suomi Hospital District");
        DISTRICT_NAMES = m;

        m = new HashMap<>();
        m.put("Ahvenanmaa", "finland/health-care-districts/ahvenanmaa");
        m.put("Etelä-Karjala", "finland/health-care-districts/etela-karjala");
        m.put("Etelä-Pohjanmaa", "finland/health-care-districts/etela-pohjanmaa");
        m.put("Etelä-Savo", "finland/health-care-districts/etela-savo");
        m.put("HUS", "finland/health-care-districts/hus");
        m.put("Itä-Savo", "finland/health-care-districts/ita-savo");
        m.put("Kainuu", "finland/health-care-districts/kainuu");
        m.put("Kanta-Häme", "finland/health-care-districts/kanta-hame");
        m.put("Keski-Pohjanmaa", "finland/health-care-districts/keski-pohjanmaa");
        m.put("Keski-Suomi", "finland/health-care-districts/keski-suomi");
        m.put("Kymenlaakso", "finland/health-care-districts/kymenlaakso");
        m.put("Lappi", "finland/health-care-districts/lappi");
        m.put("Länsi-Pohja", "finland/health-care-districts/lansi-pohja");
        m.put("Päijät-Häme", "finland/health-care-districts/paijat-hame");
        m.put("Pirkanmaa", "finland/health-care-districts/pirkanmaa");
        m.put("Pohjois-Karjala", "finland/health-care-districts/pohjois-karjala");
        m.put("Pohjois-Pohjanmaa", "finland/health-care-districts/pohjois-pohjanmaa");
        m.put("Pohjois-Savo", "finland/health-care-districts/pohjois-savo");
        m.put("Satakunta", "finland/health-care-districts/satakunta");
        m.put("Vaasa", "finland/health-care-districts/vaasa");
        m.put("Varsinais-Suomi", "finland/health-care-districts/varsinais-suomi");
        HS_DISTRICT_ID_TO_CANONICAL_IDS = m;
    }

    public HSAPIToCovidJSONConverter() {
    }

    /**
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("STARTING THE APPLICATION");
        SpringApplication.run(HSAPIToCovidJSONConverter.class, args);
        LOGGER.info("APPLICATION FINISHED");
    }


    private static Feature getTotalInfectionsForDistrictObservation(String districtId) {
        Feature retval = null;
        if (districtId != null) {
            retval = getObservationTemplate("totalInfectionCount");
            List<ZonedDateTime> dates = datesByDistrict.get(districtId);
            List<Integer> values = confirmedCasesByDistrict.get(districtId);
            if (!values.isEmpty() && !dates.isEmpty()) {
                Integer total = values.stream().reduce(0, (t, i) -> i!=null?t+i:t);
                ZonedDateTime phenomenonTimeStart = dates.get(0);
                ZonedDateTime phenomenonTimeEnd = dates.get(dates.size() - 1);
                ZonedDateTime resultTime = ZonedDateTime.now();
                retval.setGeometry(getDistrictGeometry(districtId));
                retval.setId(UUID.randomUUID().toString());
                retval.getProperties().put("ultimateFeatureOfInterestName", DISTRICT_NAMES.get(districtId));
                retval.getProperties().put("phenomenonTimeStart", phenomenonTimeStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("phenomenonTimeEnd", phenomenonTimeEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("resultTime", resultTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("result", total);
            } else  {
                LOGGER.warning("No data for district '" + districtId + "'");
            }
        } else {
            LOGGER.warning("Unknown district '" + districtId + "'");
        }
        return retval;
    }

    private static Feature getInfectionTimeseriesForDistrictObservation(String districtId) {
        Feature retval = null;
        if (districtId != null) {
            retval = getObservationTemplate("infectionTimeseries");
            List<ZonedDateTime> dates = datesByDistrict.get(districtId);
            List<Integer> values = confirmedCasesByDistrict.get(districtId);
            if (!values.isEmpty() && !dates.isEmpty()) {
                ZonedDateTime phenomenonTimeStart = dates.get(0);
                ZonedDateTime phenomenonTimeEnd = dates.get(dates.size() - 1);
                ZonedDateTime resultTime = ZonedDateTime.now();
                List<String> zonedDates = dates.stream().map((d) -> {
                    return d.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                }).collect(Collectors.toList());
                retval.setGeometry(getDistrictGeometry(districtId));
                retval.setId(UUID.randomUUID().toString());
                retval.getProperties().put("ultimateFeatureOfInterestName", DISTRICT_NAMES.get(districtId));
                retval.getProperties().put("phenomenonTimeStart", phenomenonTimeStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("phenomenonTimeEnd", phenomenonTimeEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("resultTime", resultTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                retval.getProperties().put("timeStep", zonedDates.toArray());
                retval.getProperties().put("result", values.toArray());
            } else  {
                LOGGER.warning("No data for district '" + districtId + "'");
            }
        } else {
            LOGGER.warning("Unknown district '" + districtId + "'");
        }
        return retval;
    }

    private static void loadDataHSPreProcessed() throws MalformedURLException, IOException {
        JsonNode root = getHSData();
        JsonNode confirmed = root.path("confirmed");
        Iterator<String> districtIds = confirmed.fieldNames();
        JsonNode timeseries = null;
        JsonNode dateNode;
        JsonNode valueNode;
        while (districtIds.hasNext()) {
            String hsDistrictId = districtIds.next();
            String canonicalDistrictId = HS_DISTRICT_ID_TO_CANONICAL_IDS.get(hsDistrictId);
            if (canonicalDistrictId != null) {
                datesByDistrict.put(canonicalDistrictId, new ArrayList<>());
                confirmedCasesByDistrict.put(canonicalDistrictId, new ArrayList<>());
                timeseries = confirmed.path(hsDistrictId);
                int size = timeseries.size();
                for (int i=0;i<size;i++){
                    dateNode = timeseries.get(i).path("date");
                    valueNode = timeseries.get(i).path("value");
                    if (!dateNode.isMissingNode() && !valueNode.isMissingNode()) {
                        ZonedDateTime d = ZonedDateTime.parse(dateNode.asText());
                        if (d.isAfter(startDate)) {
                            datesByDistrict.get(canonicalDistrictId).add(d);
                            Integer value = null;
                            if (valueNode.isNull()) {
                                value = null;
                            } else {
                                value = valueNode.asInt();
                            }
                            confirmedCasesByDistrict.get(canonicalDistrictId).add(value);
                        }
                    }
                }
            } 
        }
    }
   
    private static JsonNode getHSData() throws IOException {
        JsonNode retval = null;
        LOGGER.info("Fetching data from HS API");
        File f = new File(cachePath, "hs_processedThlData.json");
        long cacheExpiration = f.lastModified() + cacheMaxAgeHours * 60 * 60 * 1000;
        if (f.exists() && cacheExpiration > System.currentTimeMillis()){
            LOGGER.info("Using local cache from file " + f.getAbsolutePath());
            retval = mapper.readTree(f);
        } else {
            URL toFetch = new URL(API_URL);
            LOGGER.info("Fetching from " + toFetch.toExternalForm());
            String data = IOUtils.toString(toFetch.openStream(), Charset.forName("UTF-8"));
            if (cacheMaxAgeHours > 0) {
                FileUtils.writeStringToFile(f, data, Charset.forName("UTF-8"));
            }
            retval = mapper.readTree(data);
        }
        return retval;
    }

    private static synchronized Geometry getDistrictGeometry(String id) {
        Geometry retval = geometryCache.get(id);
        if (retval == null) {
            InputStream is = HSAPIToCovidJSONConverter.class.getResourceAsStream("/regions/" + id + ".geojson");
            if (is != null) {
                try {
                    StringWriter sw = new StringWriter();
                    IOUtils.copy(is, sw, Charset.forName("UTF-8"));
                    is.close();
                    String featureContent = sw.toString();
                    Feature f = FeatureConverter.toFeature(featureContent);
                    retval = f.getGeometry();
                    if (retval != null) {
                        geometryCache.put(id, retval);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                LOGGER.warning("Unable to find geometry file for " + id);
            }
        }
        return retval;
    }

    private static synchronized Feature getObservationTemplate(String name) {
        Feature retval = null;
        String content = templateCache.get(name);
        if (content == null) {
            InputStream is = HSAPIToCovidJSONConverter.class.getResourceAsStream("/templates/" + name + ".geojson");
            if (is != null) {
                try {
                    StringWriter sw = new StringWriter();
                    IOUtils.copy(is, sw, Charset.forName("UTF-8"));
                    is.close();
                    String featureContent = sw.toString();
                    retval = FeatureConverter.toFeature(featureContent);
                    if (retval != null) {
                        templateCache.put(name, featureContent);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } else {
            retval = FeatureConverter.toFeature(content);
        }
        return retval;
    }

    @Override
    public void run(String... arg0) throws Exception {
        loadDataHSPreProcessed(); 
        FeatureCollection totalCollection = new FeatureCollection();
        FeatureCollection timeseriesCollection = new FeatureCollection();
        for (String districtName : DISTRICT_NAMES.keySet()) {
            Feature f = getTotalInfectionsForDistrictObservation(districtName);
            if (f != null) {
                totalCollection.addFeature(f);
            }
            f = getInfectionTimeseriesForDistrictObservation(districtName);
            if (f != null) {
                timeseriesCollection.addFeature(f);
            }
        }
        File totalFile = new File(outputPath, "fin_totalInfectionsByHealthCareDistrict.geojson");
        File timeseriesFile = new File(outputPath, "fin_newInfectionsTimeseriesByHealthCareDistrict.geojson");
        LOGGER.info("Writing total infection data to " + totalFile.getAbsolutePath());
        FileUtils.writeStringToFile(totalFile, FeatureConverter.toStringValue(totalCollection), Charset.forName("UTF-8"));
        LOGGER.info("Writing new infections timeseries data to " + timeseriesFile.getAbsolutePath());
        FileUtils.writeStringToFile(timeseriesFile, FeatureConverter.toStringValue(timeseriesCollection), Charset.forName("UTF-8"));
    }
}

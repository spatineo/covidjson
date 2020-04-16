---
title: CovidJSON
description: GeoJSON profile for exchange of viral infections tests, contact tracing and regional statistics data  
---

A GeoJSON data model for exchanging data for viral infection tests, contact events used for contact tracing and regional infection statistics. The model is based on OGC/ISO Observations & measurements Standard ([O&M, ISO 19156](https://www.iso.org/standard/32574.html)) concepts. Created specifically for recording and exchanging data on SARS-CoV-2 infection tests, but likely applicable also to describing test data for detecting other infectious diseases too.

The Observations and measurements defines a conceptual model for describing observation events and their results as geospatial features. This specification uses an early draft proposal version of the ISO 19156 Edition 2 data model currently under preparation in the [OGC O&M Standards Working Group](https://github.com/opengeospatial/om-swg). The O&M GeoJSON mapping follows the proposed [O&M GeoJSON encoding profile](https://github.com/opengeospatial/omsf-profile/tree/master/omsf-json) by Ilkka Rinne.

The CovidJSON provides a data model for two primary epidemia related use cases:
* [Individual test cases](#infection-tests-and-contact-tracing) with traceable locations of the sample taking, analysis of the sample, and the residential area of the tested subject, as well as proximate contact events used for infection tracing, and
* [Regional infection statistics](#regional-infection-statistics), such as numbers of infection tests carried out or number of positive infection test results.

For up-to-date SARS-CoV-2 regional statistics from Finland in CovidJSON format see [CovidJSON Data](http://data.covidjson.org/).

Initially developed by Ilkka Rinne, the chair of the OGC Observations and measurements Standards Working Group (O&M SWG) and leader of the ISO/Technical committee 211 project for revising ISO 19156. This work was done as part of the [Spatineo](https://www.spatineo.com/) contributions for the [CoronaGIS Finland](https://geoforum.fi/paikkatiedon-koronavirus-asiantuntijat/), the Finnish task expert force for leveraging GIS for helping the Finnish governmental organisations, communities and companies in mitigating the impact of the COVID-19 pandemia in 2020.

**Example 1 (Individual SARS-CoV-2 test)**
```json
{
    "type" : "Feature",
    "id": "63d55408-39a1-4284-b413-9afb1aa86b52",
    "geometry": {
        "type": "Point",
        "coordinates": [ 24.93218, 60.19897 ]
    },
    "properties": {
        "featureType": "TruthObservation",
        "phenomenonTime": "2020-03-18T12:05:00Z",
        "resultTime": "2020-03-20T12:01:25Z",
        "procedureName": "SARS coronavirus 2 RNA [Presence] in Respiratory specimen by NAA with probe detection",
        "procedureReference": "https://loinc.org/94500-6/",
        "observedPropertyTitle": "SARS-CoV-2",
        "observedProperty": "http://snomed.info/id/840533007",
        "observerName": "Digital PCR#1, HUSLAB Kamppi",
        "platformName": "HUSLAB - Laboratory of virology and immunology",
        "platformReference": "https://korona.thl.fi/tests/api/collections/facilities/items/0f4d84ec-dabf-44c8-b133-973d80cbbed2",
        "proximateFeatureOfInterestName": "Nasopharyngeal swab sample",
        "proximateFeatureOfInterestReference": "https://korona.thl.fi/tests/api/collections/samplings/items/bfed92c2-dca6-4ac0-9b4e-9ceb4ff90f42",
        "ultimateFeatureOfInterestReference": "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5",
        "result": true
    }
  },
```

**Example 2 (Infection statistics)**

```json
{
    "type" : "Feature",
    "id": "f1a63a4f-d3b5-4b24-963f-124834290b97",
    "geometry": {
        "type": "Point",
        "coordinates": [ 24.93218, 60.19897 ]
    },
    "properties": {
        "featureType": "MeasureObservation",
        "phenomenonTimeStart": "2020-03-18T00:00:00+02:00",
        "phenomenonTimeEnd": "2020-03-18T23:23:59+02:00",
        "stimulusTime": "2020-03-20T00:00:00+02:00",
        "resultTime": "2020-03-20T00:00:32+02:00",
        "procedureName": "Spatial analysis of new infections by residential area of the tested subjects",
        "procedureReference": "https://korona.thl.fi/tests/procedure/new-verified-infections-in-residential-area",
        "observedPropertyTitle": "Number of new SARS-CoV-2 infections",
        "observedProperty": "https://korona.thl.fi/tests/quantity/SARS-CoV-2-new-infections",
        "observerName": "https://thl.fi/",
        "ultimateFeatureOfInterestName": "Pasila, Helsinki, Finland",
        "ultimateFeatureOfInterestReference": "https://sws.geonames.org/642554/about.rdf",
        "result": 56
    }
```

More data examples are available under [examples](https://github.com/ilkkarinne/covidjson/tree/master/examples).

**Note**: all data contained in the examples folder is fictious. For up-to-date official information on the COVID-19 situation in Finland, see [THL COVID-19 latest updates](https://thl.fi/en/web/infectious-diseases/what-s-new/coronavirus-covid-19-latest-updates)


## Infection tests and contact tracing

These feature types support data exchange about individual tests carried out (positive or negative) on a test subject and about events time (and optionally space) when a subject has been known to be in close or proximate contact with another subject. The subjects have a unique identifier (in most cases not traceable to an actual person), and may have some basic properties (sex, age, place of residence).

The contact event would typically be used in scenarios where there is a positive test result for the infection for a subject, and there is a need to trace close or proximate contacts the subject has had with other subjects which may have caused the infection to spread. The ContactEvent modelled here may be used both for known proximate contacts within a closed or limited space during a specific time period (the geometry of the space optionally provided), or contacts recorded by proximity sensing devices, geofencing or remote sensing (the point of the closest measured inter-subject distance as the geometry optionally provided with proximity limit radius). The proximity limit and the duration of the event also give a crude estimate of the relative average speed difference of the subjects during the event (stopped to chat, or passed-by while jogging).

**Note**: In cases where the location of the contact event is not recorded / provided for privacy reasons, the ContactEvent ```geometry``` property should given with ```type``` "Polygon" and an empty ```coordinates``` property be as follows:
```json
{
    "type" : "Feature",
    "id": "fcdcb386-e6e2-4be7-ab8c-0928992456a6",
    "geometry": {
        "type": "Polygon",
        "coordinates": [ ]
    },
    "properties": {
        "featureType": "ContactEvent",
        ...
    }
}
```
This is inline with how the ```null``` geometries are recommended to be defined in GeoJSON [RFC 7946, section 3.1 "Geometry object"](https://tools.ietf.org/html/rfc7946#section-3.1):
> o  A GeoJSON Geometry object of any type other than
      "GeometryCollection" has a member with the name "coordinates".
      The value of the "coordinates" member is an array.  The structure
      of the elements in this array is determined by the type of
      geometry.  GeoJSON processors MAY interpret Geometry objects with
      empty "coordinates" arrays as null objects.


### Single testing event feature (O&M TruthObservation)

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The representative point location of the place of residence of the tested subject | |
featureType                         | O&M Observation type, always ```TruthObservation``` | "TruthObservation" |
phenomenonTime                      | Time when observed phenomenon was happening, here the time when the sample was taken | "2020-03-18T12:05:00Z" |
resultTime                          | Time of the recording or making the Observation result available, here the time when the infection result (true/false) was available/recorded | "2020-03-19T13:01:25Z" |
procedureName                       | Name of the method used for creating the Observation | "SARS coronavirus 2 RNA [Presence] in Respiratory specimen by NAA with probe detection" or "Medical diagnosis" |
procedureReference                  | Reference to the description of the method. Primary importance as an identifier of the various test methods | "https://loinc.org/94500-6/" |
observedPropertyTitle               | Title of the observed property, here the infection kind | "SARS-CoV-2 infection"
observedProperty                    | Reference to the description of the observed property | "http://snomed.info/id/840533007"
observerName                        | Kind of the sensor / analysis instrument / person acting creating the result from the sample | "Acme PCR Analyzer 2000" or "medical doctor" |
platformName                        | Name of the entity hosting the Observer | "HUSLAB - Laboratory of virology and immunology"
platformReference                   | Reference to the description of the entity hosting the Observer  (points to MedicalFacility) | "https://korona.thl.fi/tests/api/collections/facilities/items/0f4d84ec-dabf-44c8-b133-973d80cbbed2" |
proximateFeatureOfInterestName      | Name of the sampling used | "Nasopharyngeal swab sample"
proximateFeatureOfInterestReference | Reference to the description of the sampling used (points to Sampling) | "https://korona.thl.fi/tests/api/collections/samplings/items/bfed92c2-dca6-4ac0-9b4e-9ceb4ff90f42" |
ultimateFeatureOfInterestReference | Reference to the description of the ultimate target of the Observation, here the person / animal tested for (points to the Subject) | "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5" |
result                             | The result of the test, here ```true``` if infection was detected and ```false``` if it was not | true

### MedicalFacility feature

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The point location of the facility  | |
featureType                         | Feature type, always ```MedicalFacility```   | "MedicalFacility" |
name                                | Name of the facility                         | "HUSLAB - Laboratory of virology and immunology" |
city                                | Name if the city where the facility is located in | "Helsinki" |
level1AdministrativeUnit            | Name if the region within the country where the facility is located in | "Uusimaa" | 
country                             | Name if the country where the facility is located in | "Finland" |
locationReference                   | Reference to location of the facility | "https://www.geonames.org/646253/about.rdf" |

### Sampling feature

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The point location of the place where the sampling was done | |
featureType                         | Feature type, always ```Sampling```   | "Sampling" |
samplingFacilityName                | Name of the facility where the sample was taken | "HUSLAB - Kamppi" |
samplingFacilityReference           | Reference to the description of the facility where the sample was taken (points to MedicalFacility) | "https://korona.thl.fi/tests/api/collections/facilities/items/d9a1ba35-c8af-4d0d-94b7-1e314f27d7aa" |
sampleTypeName                      | Name for the kind of the sample captured during sampling | "Nasopharyngeal swab" |
sampleTypeReference                 | Identifier for the kind of the sample captured during sampling | http://snomed.info/id/258500001 |
sampledFeatureReference             | Reference to the description of the subject of the sampling, here the person / animal tested for (points to the Subject) | "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5" |
samplingTime                        | Time when the sample was taken | "2020-03-18T12:05:00Z"

### Subject feature

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The point location of the place of residence of the subject | |
featureType                         | Feature type, always ```Subject```   | "Subject" |
species                             | Name of the species tested | "human" |
ageYears                            | Age of the test subject in years | 65 |
sex                                 | Biological sex of the test subject | "male" |
placeOfResidenceName                | Name of the place of residence of the test subject | "Pasila, Helsinki, Finland" |
placeOfResidenceReference           | Reference to the description of the place of residence of the test subject | "https://sws.geonames.org/642554/about.rdf" |

### ContactEvent feature

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The point location or geometry of the place of the recorded a close or proximate contact with another subject. Provided with an empty ```coordinates``` array if no location is given | |
featureType                         | Feature type, always ```ContactEvent```   | "ContactEvent" |
enterTime                           | Beginning of the time period where the subjects were in proximate contact with each other | "2020-03-18T12:05:03Z" |
exitTime                            | End of the time period where the subjects were in proximate contact with each other | "2020-03-18T12:05:31Z"|
proximityLimit                      | Upper limit of the inter-subject distance determined as a proximate contact in meters | 20 |   
minimumDistance                     | Minimum recorded or approximated distance in meters from the other subject during the contact, if available | 0.7 |
distanceAccuracy                    | The accuracy of the minimum distance during the contact in meters | 0.5 |
subjectReference                    | Reference to the subject recording the contact or upstream in contact tracing chain (points to Subject) | "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5" |
contactedSubjectReference           | Reference to the subject downstream in contact tracing chain (points to Subject) | "https://korona.thl.fi/tests/api/collections/subjects/items/b2edfb3b-3960-421f-a7f5-1fd8f7f9ed3d" |

## Regional infection statistics

### New daily infections per region feature (O&M MeasureObservation)

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The representative point location or polygon geometry of the residential area | |
featureType                         | O&M Observation type, always ```MeasureObservation``` | "MeasureObservation" |
phenomenonTimeStart                 | Beginning of the sample collection time period over which the statistics is calculated | "2020-03-18T00:00:00+02:00" |
phenomenonTimeEnd                   | End of the sample collection time period over which the statistics is calculated | "2020-03-18T23:23:59+02:00" |
stimulusTime                        | Time of the triggering the Observation creation, here when the analysis started | "2020-03-20T00:00:00+02:00" |
resultTime                          | Time of the recording or making the Observation result available, here the time when the static spatial analysis result was recorded | "2020-03-20T00:00:32+02:00" |
procedureName                       | Name of the method used for creating the Observation | "Spatial analysis of new lab verified infections by residential area of the tested subjects" |
procedureReference                  | Reference to the description of the method. Primary importance as an identifier of the various test methods | "https://korona.thl.fi/tests/procedure/new-verified-infections-in-residential-area" |
observedPropertyTitle               | Title of the observed property, here the infection kind | "Number of new SARS-CoV-2 infections"
observedProperty                    | Reference to the description of the observed property | "https://korona.thl.fi/tests/quantity/SARS-CoV-2-new-infections"
observerName                        | Organization responsible for the analysis | "https://thl.fi/" |
ultimateFeatureOfInterestName       | Name of the ultimate target of the Observation, here the area of residence of a subset of tested subjects | "Pasila, Helsinki, Finland",
ultimateFeatureOfInterestReference | Reference to the description of the ultimate target of the Observation, here the area of residence of a subset of tested subjects | "https://sws.geonames.org/642554/about.rdf" |
result                             | The result of the observation, here the number of new infections in the population living within the residential area during the specified sample collection time period | 56

### Daily infection timeseries feature (O&M MeasureTimeseriesObservation)
The change in the number of positive tests over time can also be of daily test results can also be included as the results of area specific observations. This is handy when results are visualized on a timeline or chart. The array valued ```timeStep``` and ```result``` properties do require a bit more fom the interpreting code that the simple values. 

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The representative point location or polygon geometry of the residential area | |
featureType                         | O&M Observation type, always ```MeasureTimeseriesObservation``` | "MeasureTimeseriesObservation" |
phenomenonTimeStart                 | Beginning of the sample collection time period over which the daily statistics is calculated | "2020-02-24T00:00:00+02:00" |
phenomenonTimeEnd                   | End of the sample collection time period over which the daily statistics is calculated | "2020-03-01T23:23:59+02:00" |
stimulusTime                        | Time of the triggering the Observation creation, here when the analysis started | "2020-03-20T00:00:00+02:00" |
resultTime                          | Time of the recording or making the Observation result available, here the time when the static spatial analysis result was recorded | "2020-03-20T00:00:32+02:00" |
procedureName                       | Name of the method used for creating the Observation | "Spatial analysis of new lab verified infections by residential area of the tested subjects. Results aggregated by day of taking the sample" |
procedureReference                  | Reference to the description of the method. Primary importance as an identifier of the various test methods | "https://korona.thl.fi/tests/procedure/new-verified-infections-in-residential-area-daily" |
observedPropertyTitle               | Title of the observed property, here the infection kind | "Number of new SARS-CoV-2 infections"
observedProperty                    | Reference to the description of the observed property | "https://korona.thl.fi/tests/quantity/SARS-CoV-2-new-infections"
observerName                        | Organization responsible for the analysis | "https://thl.fi/" |
ultimateFeatureOfInterestName       | Name of the ultimate target of the Observation, here the area of residence of a subset of tested subjects | "Helsinki and Uusimaa Hospital District, Finland",
ultimateFeatureOfInterestReference | Reference to the description of the ultimate target of the Observation, here the area of residence of a subset of tested subjects |  |
timeStep                           | Array of date and time entries for the points in the time series | [ "2020-02-24T23:59:59Z", "2020-02-25T23:59:59Z", "2020-02-26T23:59:59Z", "2020-02-27T23:59:59Z", "2020-02-28T23:59:59Z" "2020-02-29T23:59:59Z", "2020-03-01T23:59:59Z" ]  |
result                             | The result of the observation, here array of number of new infections for each point in time included in the series | [ null, 0, null, 1, 2, 1, 1 ]

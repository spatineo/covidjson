---
title: Covid-JSON
---

**NOTE: this is a draft version**

A GeoJSON data model for viral infection testing data based on Observations & measurements standard ([O&M, ISO 19156](https://www.iso.org/standard/32574.html)) concepts. Created specifically for recording and exchanging data on SARS-CoV-2 infection tests, but likely applicable also to describing test data for detecting other infectious diseases too.

The data model is based on concepts of the international standard "Observations and measurements" (ISO 19156) that defines a conceptual model for describing observation events and their results as geospatial features. This specification uses an early draft proposal version of the ISO 19156 Edition 2 data model currently under preparation in the [OGC O&M Standards Working Group](https://github.com/opengeospatial/om-swg). The O&M GeoJSON mapping follows the proposed [O&M GeoJSON encoding profile](https://github.com/opengeospatial/omsf-profile/tree/master/omsf-json) by Ilkka Rinne.

Initially developed by Ilkka Rinne / Spatineo as part of the activities of the [CoronaGISFinland](https://geoforum.fi/paikkatiedon-koronavirus-asiantuntijat/), the Finnish task expert force for leveraging GIS for helping the Finnish governmental organisations, communities and companies in mitigating the impact of the COVID-19 pandemia in 2020.

Mock data examples are available under [examples](https://github.com/ilkkarinne/covidjson/tree/master/examples).

**Important**: all data contained in this repository is fictious. For up-to-date official information on the COVID-19 situation in Finland, see https://thl.fi/en/web/infectious-diseases/what-s-new/coronavirus-covid-19-latest-updates

## Infection statistics (positive tests) per residental area

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
result                             | The result of the observation, here array number of new infections for each point in time included in the series | [ null, 0, null, 1, 2, 1, 1 ]

## Individual infection test/diagnosis data

### Single testing event feature (O&M TruthObservation)

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The representative point location of the place of residence of the tested subject | |
featureType                         | O&M Observation type, always ```TruthObservation``` | "TruthObservation" |
phenomenonTime                      | Time when observed phenomenon was happening, here the time when the sample was taken | "2020-03-18T12:05:00Z" |
resultTime                          | Time of the recording or making the Observation result available, here the time when the infection result (true/false) was available/recorded | "2020-03-19T13:01:25Z" |
procedureName                       | Name of the method used for creating the Observation | "Reverse transcription polymerase chain reaction (rRT-PCR)" or "Medical diagnosis" |
procedureReference                  | Reference to the description of the method. Primary importance as an identifier of the various test methods | "https://korona.thl.fi/tests/procedure/rRT-PCR" |
observedPropertyTitle               | Title of the observed property, here the infection kind | "SARS-CoV-2 infection"
observedProperty                    | Reference to the description of the observed property | "https://korona.thl.fi/tests/quantity/SARS-CoV-2-infection"
observerName                        | Kind of the sensor / analysis instrument / person acting creating the result from the sample | "Acme PCR Analyzer 2000" or "medical doctor" |
platformName                        | Name of the entity hosting the Observer | "HUSLAB - Laboratory of virology and immunology"
platformReference                   | Reference to the description of the entity hosting the Observer  (points to MedicalFacility) | "https://korona.thl.fi/tests/api/collections/facilities/items/0f4d84ec-dabf-44c8-b133-973d80cbbed2" |
proximateFeatureOfInterestName      | Name of the sampling used | "Nasopharyngeal swab sample"
proximateFeatureOfInterestReference | Reference to the description of the sampling used (points to Sampling) | "https://korona.thl.fi/tests/api/collections/samplings/items/bfed92c2-dca6-4ac0-9b4e-9ceb4ff90f42" |
ultimateFeatureOfInterestReference | Reference to the description of the ultimate target of the Observation, here the person / animal tested for (points to the TestSubject) | "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5" |
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
sampleType                          | Kind of the sample captured during sampling | "Nasopharyngeal swab" |
sampledFeatureReference             | Reference to the description of the subject of the sampling, here the person / animal tested for (points to the TestSubject) | "https://korona.thl.fi/tests/api/collections/subjects/items/52da6d1b-1fa7-47ee-8044-ae4851b4d3a5" |
samplingTime                        | Time when the sample was taken | "2020-03-18T12:05:00Z"

### TestSubject feature

GeoJSON property                    | Description                                  | Example value
------------------------------------|----------------------------------------------|--------------
geometry                            | The point location of the place of residence of the tested subject | |
featureType                         | Feature type, always ```TestSubject```   | "TestSubject" |
species                             | Name of the species tested | "human" |
ageYears                            | Age of the test subject in years | 65 |
gender                              | Gender of the test subject | "male" |
placeOfResidenceName                | Name of the place of residence of the test subject | "Pasila, Helsinki, Finland" |
placeOfResidenceReference           | Reference to the description of the place of residence of the test subject | "https://sws.geonames.org/642554/about.rdf" |

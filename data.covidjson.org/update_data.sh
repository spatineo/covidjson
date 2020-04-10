#!/bin/bash
java -jar ./converter/target/covidjson-converter-1.0-SNAPSHOT.jar
aws s3 cp ./out/fin_newInfectionsTimeseriesByHealthCareDistrict.geojson s3://data.covidjson.org/
aws s3 cp ./out/fin_totalInfectionsByHealthCareDistrict.geojson s3://data.covidjson.org/
aws s3 cp index.html s3://data.covidjson.org/
aws cloudfront create-invalidation --distribution-id E2OHUOG1J75B5R --paths '/*'

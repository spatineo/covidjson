#!/bin/bash
JAVA=/usr/bin/java
AWS=$HOME/.local/bin/aws
AWS_PROFILE=covidjson
cd $HOME/git/covidjson/data.covidjson.org
$JAVA -jar ./converter/target/covidjson-converter-1.0-SNAPSHOT.jar
$AWS s3 cp ./out/fin_newInfectionsTimeseriesByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp ./out/fin_totalInfectionsByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp ./out/fin_incidenceByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp ./out/fin_testsPerPopulationByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp ./out/fin_positivesRatioByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp ./out/fin_totalTestsByHealthCareDistrict.geojson s3://data.covidjson.org/ --profile $AWS_PROFILE --content-type application/geo+json
$AWS s3 cp index.html s3://data.covidjson.org/ --profile $AWS_PROFILE
$AWS cloudfront create-invalidation --distribution-id E2OHUOG1J75B5R --paths '/*' --profile $AWS_PROFILE
cd -
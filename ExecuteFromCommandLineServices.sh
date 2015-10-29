#!/bin/sh
for i in {2..16000}
do
if (( $i%2 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%3 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%5 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%7 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=browserstack
elif (( $i%9 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%11 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%13 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
elif (( $i%17 == 0 )); then
	url=http://myrealestateplatform.org:8081
	env=local
fi
report=TestReport_"$i"_"$browser"_"$env"
echo "Running test suite iteration $i for browser: $browser, env: $env and url: $url"
mvn clean verify test -Dwebdriver.base.url=$url -Dcuke.sceneric.env=$env -Dcucumber.options="--glue classpath:com/placester/services/steps src/main/features --tags @PlacesterServicesAPI --format json-pretty:target/cucumber-report-myReport.json --format html:target/cucumber-html-report-myReport" >$report

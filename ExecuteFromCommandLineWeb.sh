#!/bin/sh
for i in {2..16000}
do
if (( $i%2 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=chrome
	process="Google Chrome"
elif (( $i%3 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=firefox
	process=Firefox
elif (( $i%5 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=safari
	process=Safari
elif (( $i%7 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=browserstack
	browser=firefox
	process=Firefox
elif (( $i%9 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=chrome
	process="Google Chrome"
elif (( $i%11 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=firefox
	process=Firefox
elif (( $i%13 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=chrome
	process="Google Chrome"
elif (( $i%17 == 0 )); then
	url=http://staging:welcome@www.myrealestateplatform.org/admin/?#login
	env=local
	browser=chrome
	process="Google Chrome"
fi
report=TestReport_"$i"_"$browser"_"$env"
echo "Running test suite iteration $i for browser: $browser, env: $env and url: $url"
mvn clean verify test -Dwebdriver.driver=$browser -Dwebdriver.safari.install=false -Dwebdriver.base.url=$url -Dcuke.sceneric.env=$env -Dunlock.setting.value=yes -Dcucumber.options="--glue classpath:com/placester/web/steps src/main/features --tags @PlacesterWeb --format json-pretty:target/cucumber-report-myReport.json --format html:target/cucumber-html-report-myReport" >$report
kill -9 `ps -ef | grep "$process" | grep -v grep | awk '{print $2}'`
done
kill -9 `ps -ef | grep ExecuteFromCommandLineAll.sh | grep -v grep | awk '{print $2}'`

#!/bin/sh
mvn clean verify -Dtest=JUnitParallelWeb -Dexecution_environment=local
./KillAllProcessInstances.sh CommandLine.sh
kill `ps -ef | grep ExecuteFromCommandLineJunit_parallel.sh | grep -v grep | awk '{print $2}'`

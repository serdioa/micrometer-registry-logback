#!/bin/bash

export JAVA_HOME=/opt/jdk1.8

mvn clean install
$JAVA_HOME/bin/java -jar target/benchmarks.jar -wi 10 -i 10 de.serdioa.micrometer.core.instrument.directlogging.TimerBenchmark 2>&1 | tee jmh-benchmarks.log

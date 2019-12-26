#!/bin/bash

export JAVA_HOME=/opt/jdk1.8
$JAVA_HOME/bin/java -jar target/benchmarks.jar -wi 10 -i 10 de.serdioa.TimerBenchmark 2>&1 | tee jmh-benchmarks.log


#!/bin/bash

# Run from Maven
# mvn spring-boot:run -Dspring.config.location=file:src/config/test-springboot.properties "$@"

# Package as runnable JAR and start
mvn package spring-boot:repackage
java -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -Dspring.config.location=file:src/config/test-springboot.properties -jar target/test-springboot-1.0-SNAPSHOT.jar "$@"

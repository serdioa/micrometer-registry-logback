package de.serdioa.micrometer.logging.base;

import org.slf4j.Logger;
import org.slf4j.Marker;


public interface LoggingMeter {

    Logger getLogger();


    Marker getTags();
}

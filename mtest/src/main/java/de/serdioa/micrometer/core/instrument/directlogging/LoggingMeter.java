package de.serdioa.micrometer.core.instrument.directlogging;

import org.slf4j.Logger;
import org.slf4j.Marker;


public interface LoggingMeter {
    Logger getLogger();

    Marker getTags();
}

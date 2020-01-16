package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Comparator;

import io.micrometer.core.instrument.Meter;


public class DefaultMeterComparator implements Comparator<Meter> {

    public static Comparator<Meter> INSTANCE = new DefaultMeterComparator();


    @Override
    public int compare(Meter first, Meter second) {
        Meter.Id firstId = first.getId();
        Meter.Id secondId = second.getId();

        int typeCompare = firstId.getType().compareTo(secondId.getType());
        if (typeCompare != 0) {
            return typeCompare;
        } else {
            return firstId.getName().compareTo(secondId.getName());
        }
    }
}

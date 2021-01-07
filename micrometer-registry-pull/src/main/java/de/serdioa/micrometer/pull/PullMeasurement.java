package de.serdioa.micrometer.pull;

import java.util.Objects;
import java.util.function.Supplier;


public class PullMeasurement {

    private final String name;
    private final Supplier<Double> supplier;


    public PullMeasurement(String name, Supplier<Double> supplier) {
        this.name = Objects.requireNonNull(name);
        this.supplier = Objects.requireNonNull(supplier);
    }


    public String getName() {
        return this.name;
    }


    public Double getValue() {
        return this.supplier.get();
    }


    @Override
    public String toString() {
        return "[" + this.name + "=" + this.getValue() + "]";
    }
}

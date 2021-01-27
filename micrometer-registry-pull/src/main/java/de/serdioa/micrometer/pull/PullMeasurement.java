package de.serdioa.micrometer.pull;

import java.util.Objects;
import java.util.function.Supplier;

import io.micrometer.core.instrument.Meter;
import lombok.Getter;


public class PullMeasurement {

    public enum Type {
        COUNT,
        MAX,
        MEAN,
        TOTAL_TIME,
        VALUE,
        PERCENTILE(true),
        HISTOGRAM(true);

        private boolean valueRequired;


        private Type() {
            this(false);
        }


        private Type(boolean valueRequired) {
            this.valueRequired = valueRequired;
        }


        public boolean isValueRequired() {
            return this.valueRequired;
        }
    }


    @Getter
    public static class Id {

        private final Meter.Type meterType;
        private final Type type;
        private final Double value;


        private Id(Meter.Type meterType, Type type, Double value) {
            this.meterType = Objects.requireNonNull(meterType, "meterType is required");
            this.type = Objects.requireNonNull(type);
            this.value = value;

            if (this.type.isValueRequired() && this.value == null) {
                throw new IllegalArgumentException("type " + this.type + " requires a value");
            }
        }


        public static Id of(Meter.Type meterType, Type type) {
            return new Id(meterType, type, null);
        }


        public static Id of(Meter.Type meterType, Type type, Double value) {
            return new Id(meterType, type, value);
        }


        @Override
        public String toString() {
            switch (this.type) {
                case HISTOGRAM:
                case PERCENTILE:
                    return this.type.name() + "(" + this.value + ")";
                default:
                    return this.type.name();
            }
        }


        public String getConventionName(PullMeasurementNamingConvention namingConvention) {
            return namingConvention.name(this.meterType, this.type, this.value);
        }
    }

    private final Id id;
    private final Supplier<Double> supplier;


    private PullMeasurement(Id id, Supplier<Double> supplier) {
        this.id = Objects.requireNonNull(id);
        this.supplier = Objects.requireNonNull(supplier);
    }


    public static PullMeasurement of(Meter.Type meterType, Type type, Supplier<Double> supplier) {
        Id id = Id.of(meterType, type);
        return new PullMeasurement(id, supplier);
    }


    public static PullMeasurement of(Meter.Type meterType, Type type, Double value, Supplier<Double> supplier) {
        Id id = Id.of(meterType, type, value);
        return new PullMeasurement(id, supplier);
    }


    public Id getId() {
        return this.id;
    }


    public String getConventionName(PullMeasurementNamingConvention namingConvention) {
        return this.id.getConventionName(namingConvention);
    }


    public Double getValue() {
        return this.supplier.get();
    }


    @Override
    public String toString() {
        return "[" + this.id + "=" + this.getValue() + "]";
    }
}

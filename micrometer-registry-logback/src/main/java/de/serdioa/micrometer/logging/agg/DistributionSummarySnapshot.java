package de.serdioa.micrometer.logging.agg;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class DistributionSummarySnapshot {

    @Getter
    protected final HistogramSnapshot histogramSnapshot;


    public DistributionSummarySnapshot(DistributionSummary distributionSummary) {
        this.histogramSnapshot = distributionSummary.takeSnapshot();
    }
}

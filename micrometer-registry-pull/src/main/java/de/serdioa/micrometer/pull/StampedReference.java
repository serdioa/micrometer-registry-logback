package de.serdioa.micrometer.pull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@AllArgsConstructor
/* package private */ class StampedReference<T> {
    private final long stamp;
    private final T reference;
}

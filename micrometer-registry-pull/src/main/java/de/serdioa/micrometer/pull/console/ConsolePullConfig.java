package de.serdioa.micrometer.pull.console;

import de.serdioa.micrometer.pull.PullConfig;


public interface ConsolePullConfig extends PullConfig {

    ConsolePullConfig DEFAULT = k -> null;


    default String prefix() {
        return "consolepull";
    }
}

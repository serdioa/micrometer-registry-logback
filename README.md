JSON logging registry for Micrometer
====================================

This project provides an implementation of a [Micrometer](https://micrometer.io) registry which writes metrics into
a log file in JSON format. Logging relies on [Logback](http://logback.qos.ch) with
[Logstash Encoder](https://github.com/logstash/logstash-logback-encoder) for formatting JSON.

A typical output may look like this:

```
{"ts":"2000-01-01T00:00:00.000+00:00","metric":"metrics.counter","val":{"t":"cnt","cnt":12345.0}}
{"ts":"2000-01-01T00:00:00.000+00:00","metric":"metrics.gauge","val":{"t":"g","val":0.12345}}
```

Besides the new registry implementation, this project provides a spring-boot autoconfiguration for the new registry.

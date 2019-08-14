/*
 * --------------------------------------------------------------------------------------------
 *   Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *   Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 *  --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.examples;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.MetricBatch;
import com.newrelic.telemetry.MetricBatchSender;
import com.newrelic.telemetry.MetricBuffer;
import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.Summary;
import com.newrelic.telemetry.TelemetryClient;
import java.net.InetAddress;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * This example shows how to use the RetryingTelemetrySender to handle standard error conditions.
 *
 * <p>It also demonstrates that a single MetricBatch can contain metrics of different types.
 *
 * <p>To run this example, provide 2 command line args, the first is the URL to the metric ingest
 * endpoint, and the 2nd is the Insights Insert key.
 */
public class TelemetryClientExample {

  public static void main(String[] args) throws Exception {
    URI metricApiEndpoint = URI.create(args[0]);
    String insightsInsertKey = args[1];

    MetricBatchSender batchSender =
        SimpleMetricBatchSender.builder(insightsInsertKey, Duration.of(10, ChronoUnit.SECONDS))
            .uriOverride(metricApiEndpoint)
            .build();

    TelemetryClient sender = new TelemetryClient(batchSender);

    Attributes commonAttributes = new Attributes().put("exampleName", "RetryingExample");
    commonAttributes.put("host", InetAddress.getLocalHost().getHostName());
    commonAttributes.put("appName", "testApplication");
    commonAttributes.put("environment", "staging");

    long startTime = System.currentTimeMillis();

    MetricBuffer metricBuffer = new MetricBuffer(commonAttributes);
    metricBuffer.addMetric(
        new Gauge("temperatureC", 44d, startTime, new Attributes().put("room", "kitchen")));
    metricBuffer.addMetric(
        new Gauge("temperatureC", 25d, startTime, new Attributes().put("room", "bathroom")));
    metricBuffer.addMetric(
        new Gauge("temperatureC", 10d, startTime, new Attributes().put("room", "basement")));

    metricBuffer.addMetric(
        new Count(
            "bugsSquashed",
            5d,
            startTime,
            System.currentTimeMillis(),
            new Attributes().put("project", "JAVA")));

    metricBuffer.addMetric(
        new Summary(
            "throughput", 25, 100, 1, 10, startTime, System.currentTimeMillis(), new Attributes()));

    MetricBatch batch = metricBuffer.createBatch();

    // The retrying sender uses the recommended techniques for responding to errors from the
    // New Relic APIs. It uses a background thread to schedule the sending, handling retries
    // transparently.
    sender.send(batch);

    // make sure to shutdown the sender, else the background Executor will stop the program from
    // exiting.
    sender.shutdown();
  }
}

package com.newrelic.telemetry.json;

import static java.lang.Double.isFinite;

import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import com.newrelic.telemetry.Summary;
import com.newrelic.telemetry.Telemetry;
import com.newrelic.telemetry.Telemetry.Type;
import com.newrelic.telemetry.TelemetryBatch;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetricBatchJsonTelemetryBlockWriter implements JsonTelemetryBlockWriter {

  private final MetricToJson metricToJson;

  public MetricBatchJsonTelemetryBlockWriter(MetricToJson metricToJson) {
    this.metricToJson = metricToJson;
  }

  @Override
  public <T extends Telemetry> void appendTelemetry(
      TelemetryBatch<T> batch, StringBuilder builder) {

    if (!Type.METRIC.equals(batch.getType())) {
      throw new UnsupportedOperationException(
          "Invalid batch type. Expected " + Type.METRIC + " but got " + batch.getType());
    }

    builder.append("\"metrics\":").append("[");
    Collection<Metric> metrics = (Collection<Metric>) batch.getTelemetry();

    builder.append(
        metrics
            .stream()
            .filter(this::isValid)
            .map(this::toJsonString)
            .collect(Collectors.joining(",")));

    builder.append("]");
  }

  private boolean isValid(Metric metric) {
    return typeDispatch(
        metric,
        count -> isFinite((count.getValue())),
        gauge -> isFinite((gauge.getValue())),
        summary ->
            isFinite(summary.getMax()) && isFinite(summary.getMin()) && isFinite(summary.getSum()));
  }

  private String toJsonString(Metric metric) {
    return typeDispatch(
        metric,
        metricToJson::writeCountJson,
        metricToJson::writeGaugeJson,
        metricToJson::writeSummaryJson);
  }

  private <T> T typeDispatch(
      Metric metric,
      Function<Count, T> countFunction,
      Function<Gauge, T> gaugeFunction,
      Function<Summary, T> summaryFunction) {
    if (metric instanceof Count) {
      return countFunction.apply((Count) metric);
    }
    if (metric instanceof Gauge) {
      return gaugeFunction.apply((Gauge) metric);
    }
    if (metric instanceof Summary) {
      return summaryFunction.apply((Summary) metric);
    }
    throw new UnsupportedOperationException("Unknown metric type: " + metric.getClass());
  }
}

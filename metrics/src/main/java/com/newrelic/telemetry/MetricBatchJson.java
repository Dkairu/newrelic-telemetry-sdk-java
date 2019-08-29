package com.newrelic.telemetry;

import com.newrelic.telemetry.Telemetry.Type;
import com.newrelic.telemetry.TelemetryBatchJson.JsonCommonBlockWriter;
import com.newrelic.telemetry.TelemetryBatchJson.JsonTelemetryBlockWriter;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Double.isFinite;

public class MetricBatchJson implements JsonCommonBlockWriter, JsonTelemetryBlockWriter {

  private final AttributesJson attributesJson;
  private final MetricJsonGenerator metricJsonGenerator;

    MetricBatchJson(MetricJsonGenerator metricJsonGenerator, AttributesJson attributesJson) {
    this.attributesJson = attributesJson;
    this.metricJsonGenerator = metricJsonGenerator;
  }

  public static TelemetryBatchJson build(
      MetricJsonGenerator metricJsonGenerator, AttributesJson attributesJson) {
    MetricBatchJson metricBatchJson = new MetricBatchJson(metricJsonGenerator, attributesJson);
    return new TelemetryBatchJson(metricBatchJson, metricBatchJson);
  }

  @Override
  public <T extends Telemetry> void appendCommonJson(
      TelemetryBatch<T> batch, StringBuilder builder) {
    if (!batch.getCommonAttributes().asMap().isEmpty()) {
      builder
          .append("\"common\":")
          .append("{")
          .append("\"attributes\":")
          .append(attributesJson.toJson(batch.getCommonAttributes().asMap()))
          .append("}");
    }
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
        metricJsonGenerator::writeCountJson,
        metricJsonGenerator::writeGaugeJson,
        metricJsonGenerator::writeSummaryJson);
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

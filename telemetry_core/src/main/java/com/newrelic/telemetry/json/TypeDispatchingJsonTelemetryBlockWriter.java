package com.newrelic.telemetry.json;

import com.newrelic.telemetry.Telemetry;
import com.newrelic.telemetry.TelemetryBatch;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.spans.Span;
import com.newrelic.telemetry.spans.SpanBatch;

public class TypeDispatchingJsonTelemetryBlockWriter {

  private final JsonTelemetryBlockWriter<Metric, MetricBatch> mainBodyMetricsWriter;
  private final JsonTelemetryBlockWriter<Span, SpanBatch> mainBodySpanWriter;

  public TypeDispatchingJsonTelemetryBlockWriter(
      JsonTelemetryBlockWriter<Metric, MetricBatch> mainBodyMetricsWriter,
      JsonTelemetryBlockWriter<Span, SpanBatch> mainBodySpanWriter) {
    this.mainBodyMetricsWriter = mainBodyMetricsWriter;
    this.mainBodySpanWriter = mainBodySpanWriter;
  }

  public <S extends Telemetry, T extends TelemetryBatch<S>> void appendTelemetryJson(
      T batch, StringBuilder builder) {
    chooseMainBodyWrite(batch).appendTelemetryJson(batch, builder);
  }

  @SuppressWarnings("unchecked")
  private <S extends Telemetry, T extends TelemetryBatch<S>>
      JsonTelemetryBlockWriter<S, T> chooseMainBodyWrite(T batch) {
    switch (batch.getType()) {
      case METRIC:
        return (JsonTelemetryBlockWriter<S, T>) mainBodyMetricsWriter;
      case SPAN:
        return (JsonTelemetryBlockWriter<S, T>) mainBodySpanWriter;
    }
    throw new UnsupportedOperationException("Unhandled telemetry batch type: " + batch.getType());
  }
}

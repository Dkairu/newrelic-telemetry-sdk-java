## Telemetry Examples

This module contains a set of examples on how to use the telemetry SDK. 
They are full examples, which should compile and be runnable as-is. 

You will need to provide your Insights Insert Key and the New Relic Metric API endpoint as 
command-line parameters to the programs.

### The examples

#### [GaugeExample.java](src/main/java/com/newrelic/telemetry/examples/GaugeExample.java)

This is an example of how to write Gauge metrics to New Relic using the SDK.

#### [SummaryExample.java](src/main/java/com/newrelic/telemetry/examples/SummaryExample.java)  

This is an example of how to write Summary metrics to New Relic using the SDK.

#### [CountExample.java](src/main/java/com/newrelic/telemetry/examples/CountExample.java)

This is an example of how to write Count metrics to New Relic using the SDK.

#### [BoundaryExample.java](src/main/java/com/newrelic/telemetry/examples/BoundaryExample.java)

This is an example of pushing the boundaries of what is accepted by the New Relic APIs,
and how you can see what happens when things go wrong.

#### [SpanExample.java](src/main/java/com/newrelic/telemetry/examples/SpanExample.java)

This example shows you how you can send spans to the New Relic trace ingest api.
It demonstrates how to use the `SimpleSpanBatchSender` to easily create a `SpanBatchSender`
that can be fed a `SpanBatch`.

#### [TelemetryClientExample.java](src/main/java/com/newrelic/telemetry/examples/TelemetryClientExample.java)

This is an example of how to use the provided `com.newrelic.telemetry.RetryingTelemetrySender` to handle
errors in the recommended way.
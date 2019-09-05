/*
 * --------------------------------------------------------------------------------------------
 *   Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *   Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 *  --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.spans;

import com.newrelic.telemetry.Response;
import com.newrelic.telemetry.exceptions.ResponseException;
import com.newrelic.telemetry.spans.json.SpanBatchMarshaller;
import com.newrelic.telemetry.transport.BatchDataSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages the sending of {@link SpanBatch} instances to the New Relic Spans API. */
public class SpanBatchSender {

  private static final Logger logger = LoggerFactory.getLogger(SpanBatchSender.class);

  private final SpanBatchMarshaller marshaller;
  private final BatchDataSender sender;

  SpanBatchSender(SpanBatchMarshaller marshaller, BatchDataSender sender) {
    this.marshaller = marshaller;
    this.sender = sender;
  }

  /**
   * Send a batch of spans to New Relic.
   *
   * @param batch The batch to send. This batch will be drained of accumulated spans as a part of
   *     this process.
   * @return The response from the ingest API.
   * @throws ResponseException In cases where the batch is unable to be successfully sent, one of
   *     the subclasses of {@link ResponseException} will be thrown. See the documentation on that
   *     hierarchy for details on the recommended ways to respond to those exceptions.
   */
  public Response sendBatch(SpanBatch batch) throws ResponseException {
    if (batch == null || batch.size() == 0) {
      logger.debug("Tried to send a null or empty span batch");
      return new Response(202, "Ignored", "Empty batch");
    }
    logger.debug(
        "Sending a span batch (number of spans: {}) to the New Relic span ingest endpoint)",
        batch.size());
    String json = marshaller.toJson(batch);
    return sender.send(json);
  }

  public static SpanBatchSenderBuilder builder() {
    return new SpanBatchSenderBuilder();
  }
}

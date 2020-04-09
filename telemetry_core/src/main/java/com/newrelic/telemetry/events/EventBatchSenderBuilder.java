/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.telemetry.events;

import com.newrelic.telemetry.AbstractSenderBuilder;
import com.newrelic.telemetry.events.json.EventBatchMarshaller;
import com.newrelic.telemetry.transport.BatchDataSender;
import com.newrelic.telemetry.util.Utils;
import java.net.URL;

public class EventBatchSenderBuilder extends AbstractSenderBuilder<EventBatchSenderBuilder> {

  private static final String eventsPath = "/v1/accounts/events";
  private static final String DEFAULT_URL = "https://trace-api.newrelic.com/";

  public EventBatchSender build() {
    Utils.verifyNonNull(apiKey, "API key cannot be null");
    Utils.verifyNonNull(httpPoster, "an HttpPoster implementation is required.");

    URL url = getOrDefaultSendUrl();

    EventBatchMarshaller marshaller = new EventBatchMarshaller();

    BatchDataSender sender =
        new BatchDataSender(httpPoster, apiKey, url, auditLoggingEnabled, secondaryUserAgent);

    return new EventBatchSender(marshaller, sender);
  }

  @Override
  protected String getDefaultUrl() {
    return DEFAULT_URL;
  }

  @Override
  protected String getBasePath() {
    return eventsPath;
  }
}

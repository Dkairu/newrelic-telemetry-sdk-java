/*
 * ---------------------------------------------------------------------------------------------
 *   Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *   Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 *  --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry;

import static com.newrelic.telemetry.SimpleSpanBatchSender.build;
import static com.newrelic.telemetry.SimpleSpanBatchSender.builder;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class SimpleSpanBatchSenderTest {

  @Test
  void testBuilders() throws Exception {
    assertNotNull(builder("abc123", ofSeconds(5)));
    assertNotNull(builder("abc123"));
    assertNotNull(build("abc123"));
    assertNotNull(build("abc123", Duration.ofSeconds(5)));
  }
}

package com.newrelic.telemetry.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Response;
import com.newrelic.telemetry.events.json.EventBatchMarshaller;
import com.newrelic.telemetry.exceptions.RetryWithSplitException;
import com.newrelic.telemetry.transport.BatchDataSender;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EventBatchSenderTest {

  @Test
  void testSplitSend() throws Exception {
    Event el = new Event("xxx", null, System.currentTimeMillis(), "svcA", "A");
    Event el2 = new Event("yyy", null, System.currentTimeMillis(), "svcA", "A");
    List<Event> events = new ArrayList<>();
    events.add(el);
    events.add(el2);
    EventBatch batch = new EventBatch(events, new Attributes().put("j", "k"));

    String json = "{a great document}";
    EventBatchMarshaller marshaller = mock(EventBatchMarshaller.class);
    when(marshaller.toJson(any())).thenReturn(json);

    Response ok = new Response(200, "OK", "yup");
    BatchDataSender sender = mock(BatchDataSender.class);
    when(sender.send(json)).thenThrow(RetryWithSplitException.class).thenReturn(ok);

    EventBatchSender testClass = new EventBatchSender(marshaller, sender);

    Response result = testClass.sendBatch(batch);
    assertEquals(ok, result);
    verify(sender, times(3)).send(any());
  }

  @Test
  void testEmptyBatch() throws Exception {
    EventBatchSender testClass = new EventBatchSender(null, null);
    EventBatch batch = new EventBatch(Collections.emptyList(), new Attributes());
    Response response = testClass.sendBatch(batch);
    assertEquals(202, response.getStatusCode());
  }
}

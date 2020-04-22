package com.newrelic.telemetry;

import com.newrelic.telemetry.http.HttpPoster;
import com.newrelic.telemetry.http.HttpResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;

/** Implementation of the HttpPoster interface using an Java 11 JDK Http client. */
public class Java11HttpPoster implements HttpPoster {
  private final HttpClient httpClient;

  /** Create a Java11HttpPoster with your own object. */
  public Java11HttpPoster(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /** Create a default Java11HttpPoster with a custom call timeout. */
  public Java11HttpPoster(Duration callTimeout) {
    this.httpClient = HttpClient.newBuilder().connectTimeout(callTimeout).build();
  }

  /** Create a default Java11HttpPoster. */
  public Java11HttpPoster() {
    this.httpClient = HttpClient.newBuilder().build();
  }

  @Override
  public HttpResponse post(URL url, Map<String, String> headers, byte[] body, String mediaType)
      throws IOException {

    try {
      var builder =
          HttpRequest.newBuilder(url.toURI()).POST(HttpRequest.BodyPublishers.ofByteArray(body));
      headers.forEach((k, v) -> builder.header(k, v));
      builder.header("Content-Type", mediaType);
      var req = builder.build();

      var response =
          httpClient.send(
              req, java.net.http.HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));

      return toSdkResponse(response);
    } catch (URISyntaxException | InterruptedException e) {
      throw new IOException(e);
    }
  }

  public static HttpResponse toSdkResponse(java.net.http.HttpResponse actual) {
    return new HttpResponse(
        actual.body().toString(),
        actual.statusCode(),
        "" + actual.statusCode(),
        actual.headers().map());
  }

  public static SpanBatchSenderFactory spanSenderFactory() {
    return SpanBatchSenderFactory.ofSender(d -> new Java11HttpPoster(d));
  }

  public static MetricBatchSenderFactory metricSenderFactory() {
    return MetricBatchSenderFactory.ofSender(d -> new Java11HttpPoster(d));
  }
}

# New Relic Java Telemetry SDK 
The New Relic Java Telemetry SDK for sending telemetry data to New Relic.
The current SDK supports sending dimensional metrics to the Metric API.

Why is this cool?

Dimensional Metrics in New Relic! No agent required. Instrument to your heart's content!

Most recently published version : 0.2.0

### Getting Started: Requirements

* Java 8 or greater
* For IDEA:
    * lombok plugin installed
    * annotation processing enabled for the project
* Docker & docker-compose must be installed for integration testing

### Sample code

Fully runnable examples of how to use the SDK are found in the [telemetry_examples](telemetry_examples) module.

### Building
CI builds are run on Azure Pipelines: 
[![Build Status](https://dev.azure.com/newrelic-builds/java/_apis/build/status/PR%20build%20for%20java%20telemetry?branchName=master)](https://dev.azure.com/newrelic-builds/java/_build/latest?definitionId=8&branchName=master)

The project uses gradle 5 for building, and the gradle wrapper is provided.

To compile, run the tests and build the jars:

`$ ./gradlew build`

### Integration Testing

End-to-end integration tests are included. 
They are implemented with the testcontainers library; [mock-server](https://github.com/jamesdbloom/mockserver) provides the backend.

There are two modes to run the integration tests.
1. Run with gradle: `$ ./gradlew integration_test:test`
2. Run the `LowLevelApiIntegrationTest` class in IDEA.

### Code style
This project uses the [google-java-format](https://github.com/google/google-java-format) code style, and it is 
easily applied via an included [gradle plugin](https://github.com/sherter/google-java-format-gradle-plugin):

`$ ./gradlew googleJavaFormat verifyGoogleJavaFormat`

Please be sure to run the formatter before committing any changes. There is a `pre-commit-hook.sh` which can 
be applied automatically before commits by moving it into `.git/hooks/pre-commit`.

### Module structure:

#### `metrics`
This is the core module for sending dimensional metrics to New Relic. The library is published under maven coordinates:

`com.newrelic.telemetry:metrics`

Note: in order to use these APIs, you will need to get access to the API endpoint. 
Please contact `open-instrumentation@newrelic.com` to request access.

You will also need an Insights Insert API Key. 
Please see [New Relic Api Keys](https://docs.newrelic.com/docs/apis/getting-started/intro-apis/understand-new-relic-api-keys#user-api-key)
for more information.

#### `telemetry`
This module contains code for using all New Relic telemetry modules, gathered in one place, as well as what we 
consider "best practice" implementations of how to interact with the lower-level modules.

The `telemetry` library is published under the maven coordinates:

`com.newrelic.telemetry:telemetry`


#### `telemetry_components`
This is additional components that are useful for using the SDK. It contains reference implementations for
required components, implemented using standard open source libraries. 
The `telemetry-components` library is published under the maven coordinates:

`com.newrelic.telemetry:telemetry-components`

#### `telemetry_examples`
Example code for using the metrics and telemetry APIs.

#### `integration_test`
Integration test module. Uses docker-compose based tests to test the SDK end-to-end.

### Licensing
The New Relic Java Telemetry SDK is licensed under the Apache 2.0 License.

### Contributing
Full details are available in our CONTRIBUTING.md file. 
We'd love to get your contributions to improve the Java Telemetry SDK! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.
To execute our corporate CLA, which is required if your contribution is on behalf of a company, or if you have any questions, please drop us an email at open-source@newrelic.com. 

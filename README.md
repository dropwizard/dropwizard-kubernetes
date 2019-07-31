# dropwizard-kubernetes
[![Build Status](https://travis-ci.org/dropwizard/dropwizard-kubernetes.svg?branch=master)](https://travis-ci.org/dropwizard/dropwizard-kubernetes)
[![Coverage Status](https://img.shields.io/coveralls/dropwizard/dropwizard-kubernetes.svg)](https://coveralls.io/r/dropwizard/dropwizard-kubernetes)
[![Maven Central](https://img.shields.io/maven-central/v/io.dropwizard.modules/dropwizard-kubernetes.svg)](http://mvnrepository.com/artifact/io.dropwizard.modules/dropwizard-kubernetes)

Provides easy integration for Dropwizard applications with [the Fabric8 Kubernetes API client](https://github.com/fabric8io/kubernetes-client).

This bundle comes with out-of-the-box support for:
* Configuration (providing YAML as an option [on top of the existing ways to configure the fabric8 client](https://github.com/fabric8io/kubernetes-client#configuring-the-client))
* Kubernetes client connection lifecycle management
* Kubernetes API health checks
* Metrics instrumentation for the OKHttpClient underpinning the kubernetes-client.
* Distributed tracing integration using [Brave](https://github.com/openzipkin/brave)

For more information on the Kubernetes API, take a look at the official documentation here: https://kubernetes.io/docs/reference/#api-reference


## Usage
Add dependency on library.

Maven:
```xml
<dependency>
  <groupId>io.dropwizard.modules</groupId>
  <artifactId>dropwizard-kubernetes</artifactId>
  <version>${dropwizard-kubernetes.version}</version>
</dependency>
```

Gradle:
```groovy
compile "io.dropwizard.modules:dropwizard-kubernetes:$dropwizardKubernetesVersion"
```

## Usage
In your application's `Configuration` class, add a `KubernetesClientFactory` object:
```java
public class ExampleConfiguration extends Configuration {
    ...

    @Valid
    @NotNull
    @JsonProperty("kubernetes-client")
    private KubernetesClientFactory kubernetesClientFactory;

    public KubernetesClientFactory getKubernetesClientFactory() {
        return kubernetesClientFactory;
    }

    public void setKubernetesClientFactory(final KubernetesClientFactory kubernetesClientFactory) {
        this.kubernetesClientFactory = kubernetesClientFactory;
    }
}
```

Add a `KubernetesClientBundle` to the `Boostrap` object in your `initialize` method:
```java
private final KubernetesClientBundle<ExampleConfiguration> kubernetesClient = new KubernetesClientBundle<ExampleConfiguration>() {
    @Override
    public KubernetesClientFactory getKubernetesClientFactory(ExampleConfiguration configuration) {
        return configuration.getKubernetesClientFactory();
    }
};

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(kubernetesClient);
}

@Override
public void run(ExampleConfiguration config, Environment environment) {
    final KubernetesAPIThing kubernetesAPIThing = new KubernetesAPIThing(kubernetesClient.getKubernetesClient());
    environment.jersey().register(new KubernetesAPIThingResource(kubernetesAPIThing));
}
```

Configure your factory in your `config.yml` file:

```yaml
kubernetes-client:
  name: my-k8s-usecase
  config:
    masterUrl: https://localhost:443
    apiVersion: v1
    namespace: default
    currentContext: my-context
    userAgent: generic-crud-app
  requestConfig:
    watchReconnectInterval: 3s
    watchReconnectLimit: 25
    connectionTimeout: 10s
    requestTimeout: 4s
    rollingTimeout: 5s
    scaleTimeout: 6s
    webSocketTimeout: 3s
    webSocketPingInterval: 10s
    loggingInterval: 20s
  httpClient:
    followRedirects: false
    followSslRedirects: false
    proxy:
      url: "https://127.0.0.1:6379"
      username: admin
      password: hunter2
    security:
      trustCerts: true
      caCert:
        type: string
        caCert: abc123def456
      trustStore: src/test/resources/truststore.p12
      trustStorePassword: changeit
    interceptors:
      - type: backwards-compatibility
      - type: oauth
        oAuthToken:
          type: string
          token: 123abc456def
```


## Support
Please file bug reports and feature requests in [GitHub issues](https://github.com/dropwizard/dropwizard-kubernetes/issues).

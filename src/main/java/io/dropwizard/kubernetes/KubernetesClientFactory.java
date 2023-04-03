package io.dropwizard.kubernetes;

import brave.Tracing;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.kubernetes.config.ConfigFactory;
import io.dropwizard.kubernetes.config.RequestConfigFactory;
import io.dropwizard.kubernetes.health.KubernetesClientHealthCheck;
import io.dropwizard.kubernetes.http.OkHttpClientFactory;
import io.dropwizard.kubernetes.managed.KubernetesClientManager;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.RequestConfig;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class KubernetesClientFactory {
    public static final String DEFAULT_K8S_HEALTH_CHECK_URL = "/healthz";

    @NotNull
    @JsonProperty
    private String name;

    @Valid
    @NotNull
    @JsonProperty
    private ConfigFactory config = new ConfigFactory();

    @Valid
    @JsonProperty
    private RequestConfigFactory requestConfig;

    @Valid
    @NotNull
    @JsonProperty
    private OkHttpClientFactory httpClient = new OkHttpClientFactory();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ConfigFactory getConfig() {
        return config;
    }

    public void setConfig(final ConfigFactory config) {
        this.config = config;
    }

    public RequestConfigFactory getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(final RequestConfigFactory requestConfig) {
        this.requestConfig = requestConfig;
    }

    public OkHttpClientFactory getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(final OkHttpClientFactory httpClient) {
        this.httpClient = httpClient;
    }

    public KubernetesClient build(final MetricRegistry metrics, final LifecycleEnvironment lifecycle,
                                  final HealthCheckRegistry healthChecks, final String appName, @Nullable final Tracing tracing)
            throws Exception {
        final RequestConfig requestConf = requestConfig != null ? requestConfig.build() : null;
        final Config k8sConfig = config.build(appName, requestConf);

        if (httpClient == null) {
            return new DefaultKubernetesClient(k8sConfig);
        }

        final OkHttpClient okHttpClient = httpClient.build(k8sConfig, metrics, name, requestConf, tracing);
        final KubernetesClient client = new DefaultKubernetesClient(okHttpClient, k8sConfig);

        // manage
        lifecycle.manage(new KubernetesClientManager(client, name));

        // health checks
        final String healthCheckUrl = getHealthCheckUrl(k8sConfig);
        final KubernetesClientHealthCheck healthCheck = new KubernetesClientHealthCheck(okHttpClient, name, healthCheckUrl);
        healthChecks.register(name, healthCheck);

        return client;
    }

    protected String getHealthCheckUrl(final Config k8sConfig) {
        return k8sConfig.getMasterUrl() + DEFAULT_K8S_HEALTH_CHECK_URL;
    }
}

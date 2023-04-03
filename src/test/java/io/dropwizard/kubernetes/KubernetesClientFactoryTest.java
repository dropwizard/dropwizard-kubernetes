package io.dropwizard.kubernetes;

import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.kubernetes.health.KubernetesClientHealthCheck;
import io.dropwizard.kubernetes.managed.KubernetesClientManager;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KubernetesClientFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KubernetesClientFactory> factory =
            new YamlConfigurationFactory<>(KubernetesClientFactory.class, validator, objectMapper, "dw");
    private final MetricRegistry metrics = new MetricRegistry();
    private final LifecycleEnvironment lifecycle = mock(LifecycleEnvironment.class);
    private final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);

    private Tracing tracing;

    @BeforeEach
    public void setUp() {
        this.metrics.removeMatching(MetricFilter.ALL);
        this.tracing = Tracing.newBuilder()
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(StrictScopeDecorator.create())
                        .build())
                .build();
    }

    @Test
    public void shouldBuildAKubernetesClient() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/factory.yaml").toURI());
        final KubernetesClientFactory kubernetesClientFactory = factory.build(yaml);

        // when
        final KubernetesClient client = kubernetesClientFactory.build(metrics, lifecycle, healthChecks, "test-app", tracing);

        // then
        verify(lifecycle).manage(any(KubernetesClientManager.class));
        verify(healthChecks).register(eq("my-k8s-usecase"), any(KubernetesClientHealthCheck.class));
        assertThat(client).isNotNull();
        assertThat(client.getMasterUrl()).isEqualTo(new URL("https://localhost:443"));
    }
}

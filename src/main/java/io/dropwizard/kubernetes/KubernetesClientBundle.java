package io.dropwizard.kubernetes;

import brave.Tracing;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

public abstract class KubernetesClientBundle<T extends Configuration> implements ConfiguredBundle<T> {
    @Nullable
    private KubernetesClient kubernetesClient;

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // do nothing
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final KubernetesClientFactory kubernetesClientFactory = getKubernetesClientFactory(configuration);

        final Tracing tracing = Tracing.current();

        this.kubernetesClient = kubernetesClientFactory.build(environment.metrics(), environment.lifecycle(), environment.healthChecks(),
                environment.getName(), tracing);
    }

    public abstract KubernetesClientFactory getKubernetesClientFactory(T configuration);

    public KubernetesClient getKubernetesClient() {
        return requireNonNull(kubernetesClient);
    }
}

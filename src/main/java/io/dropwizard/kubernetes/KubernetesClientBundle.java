package io.dropwizard.kubernetes;

import brave.Tracing;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.annotation.Nullable;

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

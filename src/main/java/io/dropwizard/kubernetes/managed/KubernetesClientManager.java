package io.dropwizard.kubernetes.managed;

import io.dropwizard.lifecycle.Managed;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class KubernetesClientManager implements Managed {
    private static final Logger log = LoggerFactory.getLogger(KubernetesClientManager.class);

    private final KubernetesClient client;
    private final String name;

    public KubernetesClientManager(final KubernetesClient client, final String name) {
        this.client = requireNonNull(client);
        this.name = requireNonNull(name);
    }

    @Override
    public void start() throws Exception {
        log.info("Starting io.dropwizard.kubernetes client for name={}", name);
    }

    @Override
    public void stop() throws Exception {
        log.info("Closing io.dropwizard.kubernetes client for name={}", name);
        client.close();
    }
}

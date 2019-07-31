package io.dropwizard.kubernetes.managed;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class KubernetesClientManagerTest {
    private static final String NAME = "this-one-app";

    private final KubernetesClient k8sClient = mock(KubernetesClient.class);
    private final KubernetesClientManager k8sClientManager = new KubernetesClientManager(k8sClient, NAME);

    @Before
    public void setUp() throws Exception {
        reset(k8sClient);
    }

    @Test
    public void shouldStart() throws Exception {
        // when
        k8sClientManager.start();
    }

    @Test
    public void shouldStop() throws Exception {
        // when
        k8sClientManager.stop();

        // then
        verify(k8sClient).close();
    }
}

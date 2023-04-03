package io.dropwizard.kubernetes.health;

import com.codahale.metrics.health.HealthCheck;
import io.fabric8.kubernetes.client.KubernetesClientException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class KubernetesClientHealthCheckTest {
    private static final String NAME = "client123";
    private static final String HEALTH_URL = "https:localhost:443/healthz";

    private final OkHttpClient httpClient = mock(OkHttpClient.class);

    private final KubernetesClientHealthCheck healthCheck = new KubernetesClientHealthCheck(httpClient, NAME, HEALTH_URL);

    @BeforeEach
    public void setUp() throws Exception {
        reset(httpClient);
    }

    @Test
    public void checkShouldReturnHealthyIfResponseIsSuccessful() throws Exception {
        // given
        final Call call = mock(Call.class);
        final Response response = new Response.Builder()
                .request(new Request.Builder()
                        .get()
                        .url(HEALTH_URL)
                        .build())
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .message("blahblahblah")
                .build();
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // when
        final HealthCheck.Result result = healthCheck.check();

        // then
        assertThat(result.isHealthy()).isTrue();
    }

    @Test
    public void checkShouldReturnUnhealthyIfResponseIsUnsuccessful() throws Exception {
        // given
        final Call call = mock(Call.class);
        final Response response = new Response.Builder()
                .request(new Request.Builder()
                        .get()
                        .url(HEALTH_URL)
                        .build())
                .code(503)
                .protocol(Protocol.HTTP_1_1)
                .message("blahblahblah")
                .build();
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // when
        final HealthCheck.Result result = healthCheck.check();

        // then
        assertThat(result.isHealthy()).isFalse();
    }

    @Test
    public void checkShouldReturnUnhealthyIfClientThrowsException() throws Exception {
        // given
        final Call call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new KubernetesClientException("something bad"));

        // when
        final HealthCheck.Result result = healthCheck.check();

        // then
        assertThat(result.isHealthy()).isFalse();
    }
}

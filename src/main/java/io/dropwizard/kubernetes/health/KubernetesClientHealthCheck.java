package io.dropwizard.kubernetes.health;

import com.codahale.metrics.health.HealthCheck;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class KubernetesClientHealthCheck extends HealthCheck {
    private static final Logger log = LoggerFactory.getLogger(KubernetesClientHealthCheck.class);

    private final OkHttpClient httpClient;
    private final String name;
    private final String healthUrl;

    public KubernetesClientHealthCheck(final OkHttpClient httpClient, final String name, final String healthUrl) {
        this.httpClient = requireNonNull(httpClient);
        this.name = requireNonNull(name);
        this.healthUrl = requireNonNull(healthUrl);
    }

    @Override
    protected Result check() throws Exception {
        try {
            final Response response = httpClient.newCall(new Request.Builder()
                    .get()
                    .url(healthUrl)
                    .build())
                    .execute();

            if (!response.isSuccessful()) {
                final String errorMessage = String.format("failure: health check for name=%s against url=%s with statusCode=%d", name,
                        healthUrl, response.code());
                log.error(errorMessage);
                return Result.unhealthy(errorMessage);
            }

            log.debug("success: health check for name={} against url={}", name, healthUrl);
            return Result.healthy();
        } catch (final Exception e) {
            log.error("failure: health check for name={} against url={}", name, healthUrl, e);
            return Result.unhealthy(e);
        }
    }
}

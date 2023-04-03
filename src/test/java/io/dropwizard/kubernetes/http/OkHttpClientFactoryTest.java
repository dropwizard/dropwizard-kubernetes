package io.dropwizard.kubernetes.http;

import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.RequestConfig;
import io.fabric8.kubernetes.client.RequestConfigBuilder;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class OkHttpClientFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<OkHttpClientFactory> factory =
            new YamlConfigurationFactory<>(OkHttpClientFactory.class, validator, objectMapper, "dw");
    private final MetricRegistry metrics = new MetricRegistry();

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
    public void shouldBuildAConfigWithNoRequestConfig() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/http-client.yaml").toURI());
        final OkHttpClientFactory httpClientFactory = factory.build(yaml);

        final Config config = new ConfigBuilder().build();
        final RequestConfig requestConfig = new RequestConfigBuilder().build();

        // when
        final OkHttpClient httpClient = httpClientFactory.build(config, metrics, "app123", requestConfig, tracing);

        // then
        assertThat(httpClient).isInstanceOf(OkHttpClient.class);
        assertThat(httpClient.interceptors().size()).isEqualTo(2);
        assertThat(httpClient.proxy()).isNotNull();
        assertThat(httpClient.followRedirects()).isFalse();
        assertThat(httpClient.followRedirects()).isFalse();
    }
}

package io.dropwizard.kubernetes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.RequestConfig;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestConfigFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<RequestConfigFactory> factory =
            new YamlConfigurationFactory<>(RequestConfigFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildAConfigWithNoRequestConfig() throws Exception {
        final File yaml = new File(Resources.getResource("yaml/config/request-config.yaml").toURI());
        final RequestConfigFactory requestConfigFactory = factory.build(yaml);

        final RequestConfig config = requestConfigFactory.build();

        assertThat(config).isInstanceOf(RequestConfig.class);
        assertThat(config.getWatchReconnectInterval()).isEqualTo(3000);
        assertThat(config.getWatchReconnectLimit()).isEqualTo(25);
        assertThat(config.getConnectionTimeout()).isEqualTo(10000);
        assertThat(config.getRequestTimeout()).isEqualTo(4000);
        assertThat(config.getRollingTimeout()).isEqualTo(5000);
        assertThat(config.getScaleTimeout()).isEqualTo(6000);
        assertThat(config.getWebsocketTimeout()).isEqualTo(3000);
        assertThat(config.getWebsocketPingInterval()).isEqualTo(10000);
        assertThat(config.getLoggingInterval()).isEqualTo(20000);
    }
}

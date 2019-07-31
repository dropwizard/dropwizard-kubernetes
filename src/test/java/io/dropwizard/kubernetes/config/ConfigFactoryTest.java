package io.dropwizard.kubernetes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.RequestConfig;
import io.fabric8.kubernetes.client.RequestConfigBuilder;
import org.junit.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<ConfigFactory> factory =
            new YamlConfigurationFactory<>(ConfigFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildAConfigWithNoRequestConfig() throws Exception {
        final File yaml = new File(Resources.getResource("yaml/config/config.yaml").toURI());
        final ConfigFactory configFactory = factory.build(yaml);

        final Config config = configFactory.build("generic-crud", null);
        assertThat(config).isInstanceOf(Config.class);
        assertThat(config.getMasterUrl()).isEqualTo("https://localhost:443");
    }

    @Test
    public void shouldBuildAConfigWithRequestConfig() throws Exception {
        final File yaml = new File(Resources.getResource("yaml/config/config.yaml").toURI());
        final ConfigFactory configFactory = factory.build(yaml);

        final RequestConfig requestConfig = new RequestConfigBuilder()
                .withMaxConcurrentRequests(312)
                .build();
        final Config config = configFactory.build("generic-crud", requestConfig);
        assertThat(config).isInstanceOf(Config.class);
        assertThat(config.getMasterUrl()).isEqualTo("https://localhost:443");
        assertThat(config.getMaxConcurrentRequests()).isEqualTo(312);
    }
}

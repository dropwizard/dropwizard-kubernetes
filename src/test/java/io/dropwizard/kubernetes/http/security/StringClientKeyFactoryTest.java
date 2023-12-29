package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class StringClientKeyFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<ClientKeyFactory> factory =
            new YamlConfigurationFactory<>(ClientKeyFactory.class, validator, objectMapper, "dw");
    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(StringClientKeyFactory.class);
    }

    @Test
    public void shouldBuildAStringClientKey() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/string-client-key.yaml").toURI());
        final ClientKeyFactory clientKeyFactory = factory.build(yaml);

        // when
        final Config config = new ConfigBuilder().build();
        clientKeyFactory.addClientKeyConfigs(config);

        // then
        assertThat(clientKeyFactory).isInstanceOf(StringClientKeyFactory.class);
        assertThat(config.getClientKeyData()).isEqualTo("abc123");
        assertThat(config.getClientKeyAlgo()).isNotEmpty();
        assertThat(config.getClientKeyPassphrase()).isNotEmpty();
    }
}

package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class HostnameVerifierFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<HostnameVerifierFactory> factory =
            new YamlConfigurationFactory<>(HostnameVerifierFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildAnEnabledHostnameVerifier() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/hostname-verifier.yaml").toURI());
        final HostnameVerifierFactory hostnameVerifierFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();

        // when
        final HostnameVerifier hostnameVerifier = hostnameVerifierFactory.build(config);

        // then
        assertThat(hostnameVerifierFactory.isEnabled()).isTrue();
        assertThat(hostnameVerifier).isNotNull();
    }

    @Test
    public void shouldBuildADisabledHostnameVerifier() throws Exception {
        // given
        final HostnameVerifierFactory hostnameVerifierFactory = factory.build();
        final Config config = new ConfigBuilder().build();

        // when
        final HostnameVerifier hostnameVerifier = hostnameVerifierFactory.build(config);

        // then
        assertThat(hostnameVerifierFactory.isEnabled()).isFalse();
        assertThat(hostnameVerifier).isNotNull();
    }
}

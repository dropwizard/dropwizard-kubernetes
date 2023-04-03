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

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class FileClientCertFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<ClientCertFactory> factory =
            new YamlConfigurationFactory<>(ClientCertFactory.class, validator, objectMapper, "dw");
    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(FileClientCertFactory.class);
    }

    @Test
    public void shouldBuildAFileClientCert() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/file-client-cert.yaml").toURI());
        final ClientCertFactory clientCertFactory = factory.build(yaml);

        // when
        final Config config = new ConfigBuilder().build();
        clientCertFactory.addClientCertConfig(config);

        // then
        assertThat(clientCertFactory).isInstanceOf(FileClientCertFactory.class);
        assertThat(config.getClientCertFile()).isNotEmpty();
    }
}

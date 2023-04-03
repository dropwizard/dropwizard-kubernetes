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

public class StringCertAuthorityFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<CertAuthorityFactory> factory =
            new YamlConfigurationFactory<>(CertAuthorityFactory.class, validator, objectMapper, "dw");
    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(StringCertAuthorityFactory.class);
    }

    @Test
    public void shouldBuildAStringCertAuthority() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/string-ca.yaml").toURI());
        final CertAuthorityFactory certAuthorityFactory = factory.build(yaml);

        // when
        final Config config = new ConfigBuilder().build();
        certAuthorityFactory.addCertAuthorityConfigs(config);

        // then
        assertThat(certAuthorityFactory).isInstanceOf(StringCertAuthorityFactory.class);
        assertThat(config.getCaCertData()).isEqualTo("abc123");
    }
}

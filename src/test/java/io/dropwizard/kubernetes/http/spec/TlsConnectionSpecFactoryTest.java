package io.dropwizard.kubernetes.http.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class TlsConnectionSpecFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<ConnectionSpecFactory> factory =
            new YamlConfigurationFactory<>(ConnectionSpecFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildAClearTextConnectionSpec() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/spec/tls.yaml").toURI());
        final ConnectionSpecFactory connectionSpecFactory = factory.build(yaml);

        // when
        final Config config = new ConfigBuilder().build();

        // then
        assertThat(connectionSpecFactory).isInstanceOf(TlsConnectionSpecFactory.class);
        final TlsConnectionSpecFactory tlsConnectionSpecFactory = (TlsConnectionSpecFactory) connectionSpecFactory;
        assertThat(tlsConnectionSpecFactory.getCipherSuites()).isEqualTo(new String[0]);
        assertThat(tlsConnectionSpecFactory.getTlsVersions()).isEqualTo(new TlsVersion[] { TlsVersion.TLS_1_2 });
        assertThat(tlsConnectionSpecFactory.build(config)).isInstanceOf(ConnectionSpec.class);
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(TlsConnectionSpecFactory.class);
    }
}

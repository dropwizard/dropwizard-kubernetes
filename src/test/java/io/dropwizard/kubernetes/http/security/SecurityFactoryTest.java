package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import org.junit.jupiter.api.Test;

import java.io.File;

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SecurityFactory> factory =
            new YamlConfigurationFactory<>(SecurityFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildASecurityFactory() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/security.yaml").toURI());
        final SecurityFactory securityFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();
        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // when
        securityFactory.addSecurityConfigs(httpClientBuilder, config);

        // then
        assertThat(config.isTrustCerts()).isTrue();
        assertThat(config.getCaCertData()).isEqualTo("abc123");
        assertThat(config.getTlsVersions()).contains(TlsVersion.TLS_1_2);
        // TODO: Add more configs, which would require a valid client key, a valid trust store, and a valid client cert
    }
}

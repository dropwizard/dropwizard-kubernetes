package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import org.junit.jupiter.api.Test;

import java.io.File;

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class StringOAuthTokenFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<OAuthTokenFactory> factory =
            new YamlConfigurationFactory<>(OAuthTokenFactory.class, validator, objectMapper, "dw");
    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(StringOAuthTokenFactory.class);
    }

    @Test
    public void shouldBuildAStringOAuthTokenProvider() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/security/string-oauth-token.yaml").toURI());
        final OAuthTokenFactory oAuthTokenFactory = factory.build(yaml);

        // when
        final OAuthTokenProvider provider = oAuthTokenFactory.buildOAuthTokenProvider();

        // then
        assertThat(oAuthTokenFactory).isInstanceOf(StringOAuthTokenFactory.class);
        assertThat(provider.getToken()).isEqualTo("abc123");
    }
}

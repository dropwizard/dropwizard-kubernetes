package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import okhttp3.Interceptor;
import org.junit.jupiter.api.Test;

import java.io.File;

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAuthorizationInterceptorFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<InterceptorFactory> factory =
            new YamlConfigurationFactory<>(InterceptorFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(BasicAuthorizationInterceptorFactory.class);
    }

    @Test
    public void shouldBuildABasicAuthInterceptor() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/interceptor/basic-auth.yaml").toURI());
        final InterceptorFactory interceptorFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();

        // when
        final Interceptor interceptor = interceptorFactory.build(config);

        // then
        assertThat(interceptorFactory).isInstanceOf(BasicAuthorizationInterceptorFactory.class);
        assertThat(interceptor).isNotNull();
        assertThat(config.getUsername()).isEqualTo("admin");
        assertThat(config.getPassword()).isEqualTo("hunter2");
    }
}

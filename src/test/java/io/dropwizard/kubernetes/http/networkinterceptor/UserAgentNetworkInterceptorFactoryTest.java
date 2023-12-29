package io.dropwizard.kubernetes.http.networkinterceptor;

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

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAgentNetworkInterceptorFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<NetworkInterceptorFactory> factory =
            new YamlConfigurationFactory<>(NetworkInterceptorFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(UserAgentNetworkInterceptorFactory.class);
    }

    @Test
    public void shouldBuildAUserAgentNetworkInterceptor() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/networkinterceptor/user-agent.yaml").toURI());
        final NetworkInterceptorFactory networkInterceptorFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();

        // when
        final Interceptor interceptor = networkInterceptorFactory.build(config);

        // then
        assertThat(networkInterceptorFactory).isInstanceOf(UserAgentNetworkInterceptorFactory.class);
        assertThat(interceptor).isNotNull();
    }
}

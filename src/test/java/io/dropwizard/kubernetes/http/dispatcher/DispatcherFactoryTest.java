package io.dropwizard.kubernetes.http.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import okhttp3.Dispatcher;
import org.junit.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class DispatcherFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<DispatcherFactory> factory =
            new YamlConfigurationFactory<>(DispatcherFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildADispatcher() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/dispatcher/dispatcher.yaml").toURI());
        final DispatcherFactory dispatcherFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();

        // when
        final Dispatcher dispatcher = dispatcherFactory.build(config);

        // then
        assertThat(config.getMaxConcurrentRequests()).isEqualTo(15);
        assertThat(config.getMaxConcurrentRequestsPerHost()).isEqualTo(5);
        assertThat(dispatcher.getMaxRequests()).isEqualTo(15);
        assertThat(dispatcher.getMaxRequestsPerHost()).isEqualTo(5);
    }
}

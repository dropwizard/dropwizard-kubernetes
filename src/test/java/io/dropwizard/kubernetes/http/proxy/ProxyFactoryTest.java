package io.dropwizard.kubernetes.http.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.junit.Test;

import java.io.File;
import java.net.Proxy;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<ProxyFactory> factory =
            new YamlConfigurationFactory<>(ProxyFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildAProxy() throws Exception {
        // given
        final File yaml = new File(Resources.getResource("yaml/http/proxy/proxy.yaml").toURI());
        final ProxyFactory proxyFactory = factory.build(yaml);
        final Config config = new ConfigBuilder().build();

        // when
        final Proxy proxy = proxyFactory.build(config);

        // then
        assertThat(proxy).isNotNull();
        assertThat(proxyFactory.getPassword()).isEqualTo("hunter2");
        assertThat(proxyFactory.getUsername()).isEqualTo("admin");
        assertThat(proxyFactory.getUrl()).isNotNull();
    }
}

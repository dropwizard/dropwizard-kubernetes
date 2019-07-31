package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.fabric8.kubernetes.client.Config;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface CertAuthorityFactory extends Discoverable {
    void addCertAuthorityConfigs(final Config config);
}

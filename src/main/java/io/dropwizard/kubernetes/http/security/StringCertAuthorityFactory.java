package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import javax.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringCertAuthorityFactory implements CertAuthorityFactory {
    @NotNull
    @JsonProperty
    private String caCert;

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(final String caCert) {
        this.caCert = caCert;
    }

    @Override
    public void addCertAuthorityConfigs(final Config config) {
        config.setCaCertData(caCert);
    }
}

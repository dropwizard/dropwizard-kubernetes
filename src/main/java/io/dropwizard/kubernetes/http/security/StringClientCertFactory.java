package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import javax.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringClientCertFactory implements ClientCertFactory {
    @NotNull
    @JsonProperty
    private String clientCert;

    public String getClientCert() {
        return clientCert;
    }

    public void setClientCert(final String clientCert) {
        this.clientCert = clientCert;
    }

    @Override
    public void addClientCertConfig(final Config config) {
        config.setClientCertData(clientCert);
    }
}

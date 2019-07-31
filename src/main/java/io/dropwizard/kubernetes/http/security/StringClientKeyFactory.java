package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import javax.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringClientKeyFactory extends ClientKeyFactory {
    @NotNull
    @JsonProperty
    private String clientKey;

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(final String clientKey) {
        this.clientKey = clientKey;
    }

    @Override
    public void addClientKeyConfigs(final Config config) {
        config.setClientKeyData(clientKey);
        config.setClientKeyAlgo(clientKeyAlgo);
        config.setClientKeyPassphrase(clientKeyPassphrase);
    }
}

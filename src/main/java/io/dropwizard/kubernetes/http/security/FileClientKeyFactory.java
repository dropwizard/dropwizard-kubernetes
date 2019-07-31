package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import java.io.File;

import javax.validation.constraints.NotNull;

@JsonTypeName("file")
public class FileClientKeyFactory extends ClientKeyFactory {
    @NotNull
    @JsonProperty
    private File clientKeyFile;

    public File getClientKeyFile() {
        return clientKeyFile;
    }

    public void setClientKeyFile(final File clientKeyFile) {
        this.clientKeyFile = clientKeyFile;
    }

    @Override
    public void addClientKeyConfigs(final Config config) {
        config.setClientKeyFile(clientKeyFile.getAbsolutePath());
        config.setClientKeyAlgo(clientKeyAlgo);
        config.setClientKeyPassphrase(clientKeyPassphrase);
    }
}

package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import java.io.File;

import jakarta.validation.constraints.NotNull;

@JsonTypeName("file")
public class FileClientCertFactory implements ClientCertFactory {
    @NotNull
    @JsonProperty
    private File clientCertFile;

    public File getClientCertFile() {
        return clientCertFile;
    }

    public void setClientCertFile(final File clientCertFile) {
        this.clientCertFile = clientCertFile;
    }

    @Override
    public void addClientCertConfig(final Config config) {
        config.setClientCertFile(clientCertFile.getAbsolutePath());
    }
}

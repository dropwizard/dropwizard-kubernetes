package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;

import java.io.File;

import jakarta.validation.constraints.NotNull;

@JsonTypeName("file")
public class FileCertAuthorityFactory implements CertAuthorityFactory {
    @NotNull
    @JsonProperty
    private File caCertFile;

    public File getCaCertFile() {
        return caCertFile;
    }

    public void setCaCertFile(final File caCertFile) {
        this.caCertFile = caCertFile;
    }

    @Override
    public void addCertAuthorityConfigs(final Config config) {
        config.setCaCertFile(caCertFile.getAbsolutePath());
    }
}

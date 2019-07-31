package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.client.Config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class HostnameVerifierFactory {
    @JsonProperty
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public HostnameVerifier build(final Config config) {
        config.setDisableHostnameVerification(!enabled);

        if (!enabled || config.isTrustCerts()) {
            return (hostname, sslSession) -> true;
        }

        return HttpsURLConnection.getDefaultHostnameVerifier();
    }
}

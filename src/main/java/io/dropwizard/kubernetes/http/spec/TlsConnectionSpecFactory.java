package io.dropwizard.kubernetes.http.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonTypeName("tls")
public class TlsConnectionSpecFactory implements ConnectionSpecFactory {
    @Size(min = 1)
    @JsonProperty
    private TlsVersion[] tlsVersions = new TlsVersion[] { TlsVersion.TLS_1_2 };

    @NotNull
    @JsonProperty
    private String[] cipherSuites = new String[0];

    public TlsVersion[] getTlsVersions() {
        return tlsVersions;
    }

    public void setTlsVersions(final TlsVersion[] tlsVersions) {
        this.tlsVersions = tlsVersions;
    }

    public String[] getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(final String[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    @Override
    public ConnectionSpec build(final Config config) {
        config.setTlsVersions(tlsVersions);
        final ConnectionSpec.Builder builder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(tlsVersions);

        if (cipherSuites.length > 0) {
            builder.cipherSuites(cipherSuites);
        }

        return builder.build();
    }
}

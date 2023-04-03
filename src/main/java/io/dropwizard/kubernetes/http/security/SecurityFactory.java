package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.internal.SSLUtils;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

import java.io.File;
import java.security.GeneralSecurityException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SecurityFactory {
    @JsonProperty
    private boolean trustCerts = false;

    @Valid
    @JsonProperty
    private CertAuthorityFactory caCert;

    @Valid
    @JsonProperty
    private ClientCertFactory clientCert;

    @Valid
    @JsonProperty
    private ClientKeyFactory clientKey;

    @JsonProperty
    private File trustStore;

    @JsonProperty
    private String trustStorePassword;

    @NotNull
    @JsonProperty
    private TlsVersion tlsVersion = TlsVersion.TLS_1_2;

    public boolean isTrustCerts() {
        return trustCerts;
    }

    public void setTrustCerts(final boolean trustCerts) {
        this.trustCerts = trustCerts;
    }

    public CertAuthorityFactory getCaCert() {
        return caCert;
    }

    public void setCaCert(final CertAuthorityFactory caCert) {
        this.caCert = caCert;
    }

    public ClientCertFactory getClientCert() {
        return clientCert;
    }

    public void setClientCert(final ClientCertFactory clientCert) {
        this.clientCert = clientCert;
    }

    public ClientKeyFactory getClientKey() {
        return clientKey;
    }

    public void setClientKey(final ClientKeyFactory clientKey) {
        this.clientKey = clientKey;
    }

    public File getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(final File trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(final String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public TlsVersion getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(final TlsVersion tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    public void addSecurityConfigs(final OkHttpClient.Builder builder, final Config config) throws Exception {
        config.setTrustCerts(trustCerts);

        if (caCert != null) {
            caCert.addCertAuthorityConfigs(config);
        }

        if (clientCert != null) {
            clientCert.addClientCertConfig(config);
        }

        if (clientKey != null) {
            clientKey.addClientKeyConfigs(config);
        }

        if (trustStore != null) {
            config.setTrustStoreFile(trustStore.getAbsolutePath());
        }

        if (trustStorePassword != null) {
            config.setTrustStorePassphrase(trustStorePassword);
        }

        addTlsConfigs(builder, config);
    }

    protected void addTlsConfigs(final OkHttpClient.Builder builder, final Config config) throws Exception {
        final TrustManager[] trustManagers = SSLUtils.trustManagers(config);
        final KeyManager[] keyManagers = SSLUtils.keyManagers(config);
        if (keyManagers == null && trustManagers == null && !config.isTrustCerts()) {
            final SSLContext context = SSLContext.getInstance(tlsVersion.javaName());
            context.init(keyManagers, trustManagers, null);
            builder.sslSocketFactory(context.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        } else {
            X509TrustManager trustManager = null;
            if (trustManagers != null && trustManagers.length == 1) {
                trustManager = (X509TrustManager)trustManagers[0];
            }

            try {
                final SSLContext sslContext = SSLUtils.sslContext(keyManagers, trustManagers);
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            } catch (final GeneralSecurityException var7) {
                throw new AssertionError();
            }
        }
    }

}

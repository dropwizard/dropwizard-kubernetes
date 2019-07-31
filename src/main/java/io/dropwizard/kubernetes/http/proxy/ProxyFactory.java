package io.dropwizard.kubernetes.http.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HttpHeaders;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

import javax.validation.constraints.NotNull;

public class ProxyFactory {
    private static final Logger log = LoggerFactory.getLogger(ProxyFactory.class);

    @NotNull
    @JsonProperty
    private URL url;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Proxy build(final Config config) throws MalformedURLException {
        final URL masterUrl = new URL(config.getMasterUrl());

        if (!masterUrl.getProtocol().equals(url.getProtocol())) {
            log.error("The masterUrl={} and proxyUrl={} protocols must match", masterUrl, url);
            throw new IllegalStateException();
        }

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));
    }

    public Optional<Authenticator> buildProxyAuthenticator(final Config config) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        return Optional.of((route, response) -> {
            final String credential = Credentials.basic(config.getProxyUsername(), config.getProxyPassword());
            return response.request()
                    .newBuilder()
                    .header(HttpHeaders.PROXY_AUTHORIZATION, credential)
                    .build();
        });
    }
}

package io.dropwizard.kubernetes.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.RequestConfig;
import org.hibernate.validator.constraints.URL;

import javax.annotation.Nullable;

public class ConfigFactory {
    @URL
    @JsonProperty
    private String masterUrl;

    @JsonProperty
    private String apiVersion;

    @JsonProperty
    private String namespace;

    @JsonProperty
    private String currentContext;

    @JsonProperty
    private String userAgent;

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(final String masterUrl) {
        this.masterUrl = masterUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(final String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(final String currentContext) {
        this.currentContext = currentContext;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    public Config build(final String appName, @Nullable final RequestConfig requestConfig) {
        final Config config = Config.autoConfigure(currentContext);
        // Override with values in YAML where necessary

        if (masterUrl != null) {
            config.setMasterUrl(masterUrl);
        }

        if (apiVersion != null) {
            config.setApiVersion(apiVersion);
        }

        if (namespace != null) {
            config.setNamespace(namespace);
        }

        if (userAgent != null) {
            config.setUserAgent(userAgent);
        } else {
            config.setUserAgent(appName);
        }

        if (requestConfig != null) {
            config.setLoggingInterval(requestConfig.getLoggingInterval());
            config.setRollingTimeout(requestConfig.getRollingTimeout());
            config.setScaleTimeout(requestConfig.getScaleTimeout());
            config.setWatchReconnectInterval(requestConfig.getWatchReconnectInterval());
            config.setWatchReconnectLimit(requestConfig.getWatchReconnectLimit());
            config.setConnectionTimeout(requestConfig.getConnectionTimeout());
            config.setRequestTimeout(requestConfig.getRequestTimeout());
            config.setRollingTimeout(requestConfig.getRollingTimeout());
            config.setScaleTimeout(requestConfig.getScaleTimeout());
            config.setWebsocketTimeout(requestConfig.getWebsocketTimeout());
            config.setWebsocketPingInterval(requestConfig.getWebsocketPingInterval());
            config.setMaxConcurrentRequests(requestConfig.getMaxConcurrentRequests());
            config.setMaxConcurrentRequestsPerHost(requestConfig.getMaxConcurrentRequestsPerHost());
        }

        return config;
    }
}

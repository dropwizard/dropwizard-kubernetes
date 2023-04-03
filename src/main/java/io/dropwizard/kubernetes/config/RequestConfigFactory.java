package io.dropwizard.kubernetes.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MaxDuration;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.RequestConfig;
import io.fabric8.kubernetes.client.RequestConfigBuilder;

import java.util.concurrent.TimeUnit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RequestConfigFactory {
    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration watchReconnectInterval = Duration.seconds(1);

    @Min(-1)
    @JsonProperty
    private int watchReconnectLimit = -1;

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration connectionTimeout = Duration.seconds(10);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration requestTimeout = Duration.seconds(10);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration rollingTimeout = Duration.milliseconds(Config.DEFAULT_ROLLING_TIMEOUT);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration scaleTimeout = Duration.milliseconds(Config.DEFAULT_ROLLING_TIMEOUT);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration webSocketTimeout = Duration.milliseconds(Config.DEFAULT_WEBSOCKET_TIMEOUT);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration webSocketPingInterval = Duration.milliseconds(Config.DEFAULT_WEBSOCKET_PING_INTERVAL);

    @NotNull
    @MaxDuration(value = Integer.MAX_VALUE, unit = TimeUnit.MILLISECONDS)
    @JsonProperty
    private Duration loggingInterval = Duration.seconds(20);

    public Duration getWatchReconnectInterval() {
        return watchReconnectInterval;
    }

    public void setWatchReconnectInterval(final Duration watchReconnectInterval) {
        this.watchReconnectInterval = watchReconnectInterval;
    }

    public int getWatchReconnectLimit() {
        return watchReconnectLimit;
    }

    public void setWatchReconnectLimit(final int watchReconnectLimit) {
        this.watchReconnectLimit = watchReconnectLimit;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(final Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Duration getRollingTimeout() {
        return rollingTimeout;
    }

    public void setRollingTimeout(final Duration rollingTimeout) {
        this.rollingTimeout = rollingTimeout;
    }

    public Duration getScaleTimeout() {
        return scaleTimeout;
    }

    public void setScaleTimeout(final Duration scaleTimeout) {
        this.scaleTimeout = scaleTimeout;
    }

    public Duration getWebSocketTimeout() {
        return webSocketTimeout;
    }

    public void setWebSocketTimeout(final Duration webSocketTimeout) {
        this.webSocketTimeout = webSocketTimeout;
    }

    public Duration getWebSocketPingInterval() {
        return webSocketPingInterval;
    }

    public void setWebSocketPingInterval(final Duration webSocketPingInterval) {
        this.webSocketPingInterval = webSocketPingInterval;
    }

    public Duration getLoggingInterval() {
        return loggingInterval;
    }

    public void setLoggingInterval(final Duration loggingInterval) {
        this.loggingInterval = loggingInterval;
    }

    public RequestConfig build() {
        final RequestConfigBuilder builder = new RequestConfigBuilder()
                .withWatchReconnectInterval((int) watchReconnectInterval.toMilliseconds())
                .withWatchReconnectLimit(watchReconnectLimit)
                .withConnectionTimeout((int) connectionTimeout.toMilliseconds())
                .withRequestTimeout((int) requestTimeout.toMilliseconds())
                .withRollingTimeout(rollingTimeout.toMilliseconds())
                .withScaleTimeout(scaleTimeout.toMilliseconds())
                .withWebsocketTimeout(webSocketTimeout.toMilliseconds())
                .withWebsocketPingInterval(webSocketPingInterval.toMilliseconds())
                .withLoggingInterval((int) loggingInterval.toMilliseconds());

        return builder.build();
    }
}

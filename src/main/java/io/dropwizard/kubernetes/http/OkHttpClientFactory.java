package io.dropwizard.kubernetes.http;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.raskasa.metrics.okhttp.InstrumentedOkHttpClients;
import io.dropwizard.kubernetes.http.dispatcher.DispatcherFactory;
import io.dropwizard.kubernetes.http.interceptor.BackwardsCompatibilityInterceptorFactory;
import io.dropwizard.kubernetes.http.interceptor.InterceptorFactory;
import io.dropwizard.kubernetes.http.networkinterceptor.LoggingNetworkInterceptorFactory;
import io.dropwizard.kubernetes.http.networkinterceptor.NetworkInterceptorFactory;
import io.dropwizard.kubernetes.http.networkinterceptor.UserAgentNetworkInterceptorFactory;
import io.dropwizard.kubernetes.http.proxy.ProxyFactory;
import io.dropwizard.kubernetes.http.security.HostnameVerifierFactory;
import io.dropwizard.kubernetes.http.security.SecurityFactory;
import io.dropwizard.kubernetes.http.spec.ClearTextConnectionSpecFactory;
import io.dropwizard.kubernetes.http.spec.ConnectionSpecFactory;
import io.dropwizard.kubernetes.http.spec.TlsConnectionSpecFactory;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.RequestConfig;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class OkHttpClientFactory {
    @JsonProperty
    private boolean followRedirects = true;

    @JsonProperty
    private boolean followSslRedirects = true;

    @Valid
    @JsonProperty
    private ProxyFactory proxy;

    @Valid
    @JsonProperty
    private SecurityFactory security;

    @Valid
    @NotNull
    @JsonProperty
    private List<InterceptorFactory> interceptors = Lists.newArrayList(new BackwardsCompatibilityInterceptorFactory());

    @Valid
    @NotNull
    @JsonProperty
    private List<NetworkInterceptorFactory> networkInterceptors = Lists.newArrayList(new LoggingNetworkInterceptorFactory(),
            new UserAgentNetworkInterceptorFactory());

    @Valid
    @NotNull
    @JsonProperty List<ConnectionSpecFactory> connectionSpecs = Lists.newArrayList(new ClearTextConnectionSpecFactory(),
            new TlsConnectionSpecFactory());

    @Valid
    @NotNull
    @JsonProperty
    private DispatcherFactory dispatcher = new DispatcherFactory();

    @Valid
    @NotNull
    @JsonProperty
    private HostnameVerifierFactory hostnameVerifier = new HostnameVerifierFactory();

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowSslRedirects() {
        return followSslRedirects;
    }

    public void setFollowSslRedirects(final boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
    }

    public ProxyFactory getProxy() {
        return proxy;
    }

    public void setProxy(final ProxyFactory proxy) {
        this.proxy = proxy;
    }

    public SecurityFactory getSecurity() {
        return security;
    }

    public void setSecurity(final SecurityFactory security) {
        this.security = security;
    }

    public List<InterceptorFactory> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(final List<InterceptorFactory> interceptors) {
        this.interceptors = interceptors;
    }

    public List<NetworkInterceptorFactory> getNetworkInterceptors() {
        return networkInterceptors;
    }

    public void setNetworkInterceptors(final List<NetworkInterceptorFactory> networkInterceptors) {
        this.networkInterceptors = networkInterceptors;
    }

    public List<ConnectionSpecFactory> getConnectionSpecs() {
        return connectionSpecs;
    }

    public void setConnectionSpecs(final List<ConnectionSpecFactory> connectionSpecs) {
        this.connectionSpecs = connectionSpecs;
    }

    public DispatcherFactory getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(final DispatcherFactory dispatcher) {
        this.dispatcher = dispatcher;
    }

    public HostnameVerifierFactory getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(final HostnameVerifierFactory hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public OkHttpClient build(final Config config, final MetricRegistry metrics, final String name,
                              @Nullable final RequestConfig requestConfig, @Nullable final Tracing tracing) throws Exception {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirects)
                .followSslRedirects(followSslRedirects);

        interceptors.stream()
                .map(factory -> factory.build(config))
                .forEach(builder::addInterceptor);
        networkInterceptors.stream()
                .map(factory -> factory.build(config))
                .forEach(builder::addNetworkInterceptor);

        builder.connectionSpecs(connectionSpecs.stream()
                .map(factory -> factory.build(config))
                .collect(Collectors.toList()));

        if (security != null) {
            security.addSecurityConfigs(builder, config);
        }

        builder.hostnameVerifier(hostnameVerifier.build(config));

        if (tracing != null) {
            final HttpTracing httpTracing = HttpTracing.newBuilder(tracing).build();

            builder.dispatcher(new Dispatcher(tracing.currentTraceContext()
                    .executorService(new Dispatcher().executorService())
            ));

            builder.addNetworkInterceptor(TracingInterceptor.create(httpTracing));
        }

        if (requestConfig != null) {
            builder.callTimeout(requestConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            builder.readTimeout(requestConfig.getRequestTimeout(), TimeUnit.MILLISECONDS);
            builder.connectTimeout(requestConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            builder.writeTimeout(requestConfig.getRequestTimeout(), TimeUnit.MILLISECONDS);
            builder.pingInterval(requestConfig.getWebsocketPingInterval(), TimeUnit.MILLISECONDS);
        }

        if (proxy != null) {
            builder.proxy(proxy.build(config));
            proxy.buildProxyAuthenticator(config).ifPresent(builder::proxyAuthenticator);
        }

        builder.dispatcher(dispatcher.build(config));

        final OkHttpClient rawHttpClient = builder.build();
        return InstrumentedOkHttpClients.create(metrics, rawHttpClient, name);
    }
}

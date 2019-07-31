package io.dropwizard.kubernetes.http.dispatcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Dispatcher;

import javax.validation.constraints.Min;

public class DispatcherFactory {
    @Min(1)
    @JsonProperty
    private int maxConcurrentRequests = Config.DEFAULT_MAX_CONCURRENT_REQUESTS;

    @Min(1)
    @JsonProperty
    private int maxConcurrentRequestsPerHost = Config.DEFAULT_MAX_CONCURRENT_REQUESTS_PER_HOST;

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public void setMaxConcurrentRequests(final int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }

    public int getMaxConcurrentRequestsPerHost() {
        return maxConcurrentRequestsPerHost;
    }

    public void setMaxConcurrentRequestsPerHost(final int maxConcurrentRequestsPerHost) {
        this.maxConcurrentRequestsPerHost = maxConcurrentRequestsPerHost;
    }

    public Dispatcher build(final Config config) {
        config.setMaxConcurrentRequests(maxConcurrentRequests);
        config.setMaxConcurrentRequestsPerHost(maxConcurrentRequestsPerHost);

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxConcurrentRequests);
        dispatcher.setMaxRequestsPerHost(maxConcurrentRequestsPerHost);
        return dispatcher;
    }
}

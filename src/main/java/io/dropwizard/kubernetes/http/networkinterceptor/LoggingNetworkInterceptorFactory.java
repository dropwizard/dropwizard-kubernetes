package io.dropwizard.kubernetes.http.networkinterceptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.validation.constraints.NotNull;

@JsonTypeName("logging")
public class LoggingNetworkInterceptorFactory implements NetworkInterceptorFactory {
    @JsonProperty
    private boolean enabled = true;

    @NotNull
    @JsonProperty
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public HttpLoggingInterceptor.Level getLevel() {
        return level;
    }

    public void setLevel(final HttpLoggingInterceptor.Level level) {
        this.level = level;
    }

    @Override
    public Interceptor build(final Config config) {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }
}

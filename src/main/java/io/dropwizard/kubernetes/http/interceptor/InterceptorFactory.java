package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Interceptor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface InterceptorFactory extends Discoverable {
    Interceptor build(final Config config);
}

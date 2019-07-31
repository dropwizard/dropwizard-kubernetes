package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.utils.BackwardsCompatibilityInterceptor;
import okhttp3.Interceptor;

@JsonTypeName("backwards-compatibility")
public class BackwardsCompatibilityInterceptorFactory implements InterceptorFactory {
    @Override
    public Interceptor build(final Config config) {
        return new BackwardsCompatibilityInterceptor();
    }
}

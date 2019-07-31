package io.dropwizard.kubernetes.http.networkinterceptor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Interceptor;
import okhttp3.Request;

import javax.ws.rs.core.HttpHeaders;

@JsonTypeName("user-agent")
public class UserAgentNetworkInterceptorFactory implements NetworkInterceptorFactory {
    @Override
    public Interceptor build(final Config config) {
        return chain -> {
            final Request request = chain.request()
                    .newBuilder()
                    .header(HttpHeaders.USER_AGENT, config.getUserAgent())
                    .build();
            return chain.proceed(request);
        };
    }
}

package io.dropwizard.kubernetes.http.interceptor;

import io.fabric8.kubernetes.client.OAuthTokenProvider;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;

public class OAuthAuthorizationInterceptor implements Interceptor {

    private final OAuthTokenProvider oAuthTokenProvider;

    public OAuthAuthorizationInterceptor(final OAuthTokenProvider oAuthTokenProvider) {
        this.oAuthTokenProvider = oAuthTokenProvider;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        try {
            final Request authReq = chain.request()
                    .newBuilder()
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + oAuthTokenProvider.getToken())
                    .build();
            return chain.proceed(authReq);
        } catch (final IllegalArgumentException e) {
            // This exception may leak secrets -- we instead want to fail without leaking the auth token value
            throw new IllegalArgumentException("Unexpected character found in authorization token value");
        }
    }
}

package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.kubernetes.http.security.OAuthTokenFactory;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import okhttp3.Interceptor;
import okhttp3.Request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;

@JsonTypeName("oauth")
public class OAuthAuthorizationInterceptorFactory implements InterceptorFactory {
    @Valid
    @NotNull
    @JsonProperty
    private OAuthTokenFactory oAuthToken;

    public OAuthTokenFactory getOAuthToken() {
        return oAuthToken;
    }

    public void setOAuthToken(final OAuthTokenFactory oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    @Override
    public Interceptor build(final Config config) {
        final OAuthTokenProvider oAuthTokenProvider = oAuthToken.buildOAuthTokenProvider();
        config.setOauthTokenProvider(oAuthTokenProvider);
        return chain -> {
            final Request authReq = chain.request()
                    .newBuilder()
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + oAuthTokenProvider.getToken())
                    .build();
            return chain.proceed(authReq);
        };
    }
}

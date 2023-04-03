package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.OAuthTokenProvider;

import jakarta.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringOAuthTokenFactory implements OAuthTokenFactory {
    @NotNull
    @JsonProperty
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    @Override
    public OAuthTokenProvider buildOAuthTokenProvider() {
        return () -> token;
    }
}

package io.dropwizard.kubernetes.http.security;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.fabric8.kubernetes.client.OAuthTokenProvider;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface OAuthTokenFactory extends Discoverable {
    OAuthTokenProvider buildOAuthTokenProvider();
}

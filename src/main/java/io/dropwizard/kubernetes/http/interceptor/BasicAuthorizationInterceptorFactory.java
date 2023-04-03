package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.HttpHeaders;

@JsonTypeName("basic-auth")
public class BasicAuthorizationInterceptorFactory implements InterceptorFactory {
    @NotNull
    @JsonProperty
    private String username;

    @NotNull
    @JsonProperty
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public Interceptor build(final Config config) {
        config.setUsername(username);
        config.setPassword(password);
        return chain -> {
            final Request authReq = chain.request()
                    .newBuilder()
                    .addHeader(HttpHeaders.AUTHORIZATION, Credentials.basic(username, password))
                    .build();
            return chain.proceed(authReq);
        };
    }
}

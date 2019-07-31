package io.dropwizard.kubernetes.http.interceptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.utils.ImpersonatorInterceptor;
import okhttp3.Interceptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

@JsonTypeName("impersonation")
public class ImpersonationInterceptorFactory implements InterceptorFactory {
    @NotNull
    @JsonProperty
    private String username;

    @NotNull
    @JsonProperty
    private String[] groups = new String[0];

    @NotNull
    @JsonProperty
    private Map<String, List<String>> extras = Collections.emptyMap();

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(final String[] groups) {
        this.groups = groups;
    }

    public Map<String, List<String>> getExtras() {
        return extras;
    }

    public void setExtras(final Map<String, List<String>> extras) {
        this.extras = extras;
    }

    @Override
    public Interceptor build(final Config config) {
        final Config impersonateConfig = new ConfigBuilder()
                .withImpersonateUsername(username)
                .withImpersonateGroups(groups)
                .withImpersonateExtras(extras)
                .build();
        return new ImpersonatorInterceptor(impersonateConfig);
    }
}

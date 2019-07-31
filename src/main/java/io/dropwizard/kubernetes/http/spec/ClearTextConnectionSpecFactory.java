package io.dropwizard.kubernetes.http.spec;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.fabric8.kubernetes.client.Config;
import okhttp3.ConnectionSpec;

@JsonTypeName("clear-text")
public class ClearTextConnectionSpecFactory implements ConnectionSpecFactory {
    @Override
    public ConnectionSpec build(final Config config) {
        return ConnectionSpec.CLEARTEXT;
    }
}

package io.dropwizard.kubernetes.http.spec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.fabric8.kubernetes.client.Config;
import okhttp3.ConnectionSpec;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ConnectionSpecFactory extends Discoverable {
    ConnectionSpec build(final Config config);
}

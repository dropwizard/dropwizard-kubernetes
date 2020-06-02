package io.dropwizard.kubernetes.http.interceptor;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.kubernetes.http.security.StringOAuthTokenFactory;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import okhttp3.Interceptor;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OAuthAuthorizationInterceptorTest {

    @Test
    public void shouldNotLeakSecretsInExceptionThrowWhenAddHeaderFails() throws IOException {
        // given
        final StringOAuthTokenFactory tokenFactory = new StringOAuthTokenFactory();
        tokenFactory.setToken("Bearer this is a secret");
        final OAuthTokenProvider tokenProvider = tokenFactory.buildOAuthTokenProvider();
        final OAuthAuthorizationInterceptor interceptor = new OAuthAuthorizationInterceptor(tokenProvider);

        final Interceptor.Chain chain = mock(Interceptor.Chain.class);
        final IllegalArgumentException exception = new IllegalArgumentException("this leaks secrets");
        when(chain.request()).thenThrow(exception);

        // when
        IllegalArgumentException caughtException = null;
        try {
            interceptor.intercept(chain);
            fail();
        } catch (final IllegalArgumentException e) {
            caughtException = e;
        }

        // then
        assertThat(caughtException).isNotEqualTo(exception);
    }
}

// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.openstreetmap.josm.tools.HttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth implementation
 */
public class OAuthService {

    private final OAuth10aService service;

    OAuthService(OAuthParameters parameters) {
        this.service = new ServiceBuilder(OAuthParameters.DEFAULT_JOSM_CONSUMER_KEY)
                .apiSecret(OAuthParameters.DEFAULT_JOSM_CONSUMER_SECRET)
                .httpClient(ScribbleAdapters.createHttpClient())
                .callback("")
                .build(ScribbleAdapters.createApi10a(parameters));
    }

    /**
     * Retrieves the request token
     * @return the request token
     * @throws IOException if any I/O error occurs
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public OAuthToken getRequestToken() throws IOException, ExecutionException, InterruptedException {
        OAuth1RequestToken requestToken = service.getRequestToken();
        return new OAuthToken(requestToken);
    }

    /**
     * Retrieves the access token
     * @param requestToken the request token obtained using {@link #getRequestToken}
     * @return the access token
     * @throws IOException if any I/O error occurs
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public OAuthToken getAccessToken(OAuthToken requestToken) throws IOException, ExecutionException, InterruptedException {
        OAuth1RequestToken token = new OAuth1RequestToken(requestToken.getKey(), requestToken.getSecret());
        OAuth1AccessToken accessToken = service.getAccessToken(token, "");
        return new OAuthToken(accessToken);
    }

    /**
     * Adds OAuth parameters and signature to the given request
     * @param accessToken the access token obtained using {@link #getAccessToken}
     * @param httpClient the request to sign
     */
    public void signRequest(OAuthToken accessToken, HttpClient httpClient) {
        OAuthRequest request = new OAuthRequest(Verb.valueOf(httpClient.getRequestMethod()), httpClient.getURL().toString());
        OAuth1AccessToken token = new OAuth1AccessToken(accessToken.getKey(), accessToken.getSecret());
        service.signRequest(token, request);
        httpClient.setHeaders(request.getHeaders());
    }
}

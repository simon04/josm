// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.oauth;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.httpclient.jdk.JDKHttpFuture;
import com.github.scribejava.core.httpclient.multipart.MultipartPayload;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Adapters to make {@code com.github.scribejava} work with {@link org.openstreetmap.josm.tools.HttpClient}.
 */
interface ScribbleAdapters {

    static DefaultApi10a createApi10a(OAuthParameters parameters) {
        return new DefaultApi10a() {
            @Override
            public String getRequestTokenEndpoint() {
                return parameters.getRequestTokenUrl();
            }

            @Override
            public String getAccessTokenEndpoint() {
                return parameters.getAccessTokenUrl();
            }

            @Override
            protected String getAuthorizationBaseUrl() {
                return parameters.getAuthoriseUrl();
            }
        };
    }

    static com.github.scribejava.core.httpclient.HttpClient createHttpClient() {
        return new HttpClient();
    }

    class HttpClient implements com.github.scribejava.core.httpclient.HttpClient {
        @Override
        public void close() {
        }

        @Override
        public <T> Future<T> executeAsync(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl, byte[] bodyContents,
                                          OAuthAsyncRequestCallback<T> callback, OAuthRequest.ResponseConverter<T> converter) {

            try {
                final Response response = execute(userAgent, headers, httpVerb, completeUrl, bodyContents);
                @SuppressWarnings("unchecked") final T t = converter == null ? (T) response : converter.convert(response);
                if (callback != null) {
                    callback.onCompleted(t);
                }
                return new JDKHttpFuture<>(t);
            } catch (IOException | RuntimeException | InterruptedException | ExecutionException e) {
                if (callback != null) {
                    callback.onThrowable(e);
                }
                return new JDKHttpFuture<>(e);
            }

        }

        @Override
        public <T> Future<T> executeAsync(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl, String bodyContents,
                                          OAuthAsyncRequestCallback<T> callback, OAuthRequest.ResponseConverter<T> converter) {
            return executeAsync(userAgent, headers, httpVerb, completeUrl, bodyContents.getBytes(StandardCharsets.UTF_8), callback, converter);
        }

        @Override
        public <T> Future<T> executeAsync(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl,
                                          MultipartPayload bodyContents,
                                          OAuthAsyncRequestCallback<T> callback, OAuthRequest.ResponseConverter<T> converter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Future<T> executeAsync(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl, File bodyContents,
                                          OAuthAsyncRequestCallback<T> callback, OAuthRequest.ResponseConverter<T> converter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response execute(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl,
                                byte[] bodyContents) throws InterruptedException, ExecutionException, IOException {
            final URL url = new URL(completeUrl);
            org.openstreetmap.josm.tools.HttpClient client = org.openstreetmap.josm.tools.HttpClient.create(url, httpVerb.name());
            client.setHeaders(headers);
            client.setRequestBody(bodyContents);
            client.setReasonForRequest("OAuth");

            try {
                org.openstreetmap.josm.tools.HttpClient.Response response = client.connect();
                final int responseCode = response.getResponseCode();
                Map<String, String> responseHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                response.getHeaderFields().forEach((key, values) -> responseHeaders.put(key, values.get(0)));
                return new Response(responseCode, response.getResponseMessage(), responseHeaders, response.fetchContent());
            } catch (UnknownHostException e) {
                throw new OAuthException("The IP address of a host could not be determined.", e);
            }
        }

        @Override
        public Response execute(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl,
                                String bodyContents) throws InterruptedException, ExecutionException, IOException {
            return execute(userAgent, headers, httpVerb, completeUrl, bodyContents.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public Response execute(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl,
                                MultipartPayload multipartPayloads) throws InterruptedException, ExecutionException, IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response execute(String userAgent, Map<String, String> headers, Verb httpVerb, String completeUrl,
                                File bodyContents) throws InterruptedException, ExecutionException, IOException {
            throw new UnsupportedOperationException();
        }
    }
}

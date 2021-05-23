// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.oauth;

import java.util.Objects;

import com.github.scribejava.core.model.OAuth1Token;
import org.openstreetmap.josm.tools.CheckParameterUtil;

/**
 * An oauth token that has been obtained by JOSM and can be used to authenticate the user on the server.
 */
public class OAuthToken {

    private final String key;
    private final String secret;

    /**
     * Creates a new token
     *
     * @param key the token key
     * @param secret the token secret
     */
    public OAuthToken(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    /**
     * Creates a clone of another token
     *
     * @param other the other token. Must not be null.
     * @throws IllegalArgumentException if other is null
     */
    public OAuthToken(OAuthToken other) {
        CheckParameterUtil.ensureParameterNotNull(other, "other");
        this.key = other.key;
        this.secret = other.secret;
    }

    public OAuthToken(OAuth1Token token) {
        CheckParameterUtil.ensureParameterNotNull(token, "token");
        this.key = token.getToken();
        this.secret = token.getTokenSecret();
    }

    /**
     * Replies the token key
     *
     * @return the token key
     */
    public String getKey() {
        return key;
    }

    /**
     * Replies the token secret
     *
     * @return the token secret
     */
    public String getSecret() {
        return secret;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, secret);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OAuthToken that = (OAuthToken) obj;
        return Objects.equals(key, that.key) &&
                Objects.equals(secret, that.secret);
    }
}

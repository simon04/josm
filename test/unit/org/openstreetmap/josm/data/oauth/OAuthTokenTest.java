// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.oauth;

import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.TestUtils;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Unit tests for class {@link OAuthToken}.
 */
class OAuthTokenTest {

    /**
     * Unit test of methods {@link OAuthToken#equals} and {@link OAuthToken#hashCode}.
     */
    @Test
    void testEqualsContract() {
        TestUtils.assumeWorkingEqualsVerifier();
        EqualsVerifier.forClass(OAuthToken.class).usingGetClass().verify();
    }
}

package com.hedgehogsmind.springcouchrest.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatedAndNormalizedCouchRestConfigurationTest {

    private class EmptyConfig extends CouchRestConfigurationAdapter {
        @Override
        public String getCouchRestBasePath() {
            return null;
        }
    }

    @Test
    public void testBasePathNull() {

        final CouchRestConfiguration config = new EmptyConfig() {
            @Override
            public String getCouchRestBasePath() {
                return null;
            }
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ValidatedAndNormalizedCouchRestConfiguration(config);
        });
    }

    @Test
    public void testBasePathEmpty() {

        final CouchRestConfiguration config = new EmptyConfig() {
            @Override
            public String getCouchRestBasePath() {
                return "";
            }
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ValidatedAndNormalizedCouchRestConfiguration(config);
        });
    }

    @Test
    public void testBasePathNormalization() {
        final CouchRestConfiguration config = new EmptyConfig() {
            @Override
            public String getCouchRestBasePath() {
                return "///hello/world/////thisIsATest/";
            }
        };

        final CouchRestConfiguration normalized = new ValidatedAndNormalizedCouchRestConfiguration(config);

        Assertions.assertEquals(
                "/hello/world/thisIsATest/",
                normalized.getCouchRestBasePath(),
                "basePath normalization failed"
        );
    }

    @Test
    public void testFailOnBasePathWithoutTrailingSlash() {
        final CouchRestConfiguration config = new EmptyConfig() {
            @Override
            public String getCouchRestBasePath() {
                return "test/wo/trailingSlash";
            }
        };

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ValidatedAndNormalizedCouchRestConfiguration(config),
                "No trailing slash has been configured, expected IllegalArgumentException"
        );
    }

}

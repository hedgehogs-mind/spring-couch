package com.hedgehogsmind.springcouchrest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ValidatedAndNormalizedCouchRestConfigurationTest {

    private class EmptyConfig implements CouchRestConfiguration {
        @Override
        public String getCouchRestBasePath() {
            return null;
        }

        @Override
        public Optional<ObjectMapper> getCouchRestObjectMapper() {
            return Optional.empty();
        }

        @Override
        public String getBaseSecurityRule() {
            return "true";
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

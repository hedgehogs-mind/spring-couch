package com.hedgehogsmind.springcouch2r.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ValidatedAndNormalizedCouch2rConfigurationTest {

    private class EmptyConfig implements Couch2rConfiguration {
        @Override
        public String getCouch2rBasePath() {
            return null;
        }

        @Override
        public Optional<ObjectMapper> getCouch2rObjectMapper() {
            return Optional.empty();
        }
    }

    @Test
    public void testBasePathNull() {

        final Couch2rConfiguration config = new EmptyConfig() {
            @Override
            public String getCouch2rBasePath() {
                return null;
            }
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ValidatedAndNormalizedCouch2rConfiguration(config);
        });
    }

    @Test
    public void testBasePathEmpty() {

        final Couch2rConfiguration config = new EmptyConfig() {
            @Override
            public String getCouch2rBasePath() {
                return "";
            }
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ValidatedAndNormalizedCouch2rConfiguration(config);
        });
    }

    @Test
    public void testBasePathNormalization() {
        final Couch2rConfiguration config = new EmptyConfig() {
            @Override
            public String getCouch2rBasePath() {
                return "///hello/world/////thisIsATest/";
            }
        };

        final Couch2rConfiguration normalized = new ValidatedAndNormalizedCouch2rConfiguration(config);

        Assertions.assertEquals(
                "/hello/world/thisIsATest/",
                normalized.getCouch2rBasePath(),
                "basePath normalization failed"
        );
    }

    @Test
    public void testFailOnBasePathWithoutTrailingSlash() {
        final Couch2rConfiguration config = new EmptyConfig() {
            @Override
            public String getCouch2rBasePath() {
                return "test/wo/trailingSlash";
            }
        };

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ValidatedAndNormalizedCouch2rConfiguration(config),
                "No trailing slash has been configured, expected IllegalArgumentException"
        );
    }

}

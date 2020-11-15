package com.hedgehogsmind.springcouch2r.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatedAndNormalizedCouch2rConfigurationTest {

    private class EmptyConfig implements Couch2rConfiguration {
        @Override
        public String getCouch2rBasePath() {
            return null;
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
                return "///hello/world/////thisIsATest";
            }
        };

        final Couch2rConfiguration normalized = new ValidatedAndNormalizedCouch2rConfiguration(config);

        Assertions.assertEquals(
                "/hello/world/thisIsATest/",
                normalized.getCouch2rBasePath(),
                "basePath normalization failed"
        );
    }

}

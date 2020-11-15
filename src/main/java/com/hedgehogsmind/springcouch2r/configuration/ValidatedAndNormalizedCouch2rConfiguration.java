package com.hedgehogsmind.springcouch2r.configuration;

import com.hedgehogsmind.springcouch2r.util.Couch2rPathUtil;

/**
 * Takes an other {@link Couch2rConfiguration}, validates data and copies normalized values.
 */
public class ValidatedAndNormalizedCouch2rConfiguration implements Couch2rConfiguration {

    private final String basePath;

    public ValidatedAndNormalizedCouch2rConfiguration(final Couch2rConfiguration original) {
        this.basePath = validateAndNormalizeBasePath(original.getCouch2rBasePath());
    }

    /**
     * Checks that the basePath is neither null or empty. Replaces double slashes by single one and returns
     * path with trailing slash.
     * @param basePath BasePath to validate and normalize.
     * @return basePath with trailing slash.
     */
    public String validateAndNormalizeBasePath(final String basePath) {
        if ( basePath == null ) throw new IllegalArgumentException("Couch2r basePath must not be null");
        if ( basePath.isBlank() ) throw new IllegalArgumentException("Couch2r basePath must not be empty");

        return Couch2rPathUtil.normalizeWithTrailingSlash(basePath);
    }

    @Override
    public String getCouch2rBasePath() {
        return basePath;
    }
}

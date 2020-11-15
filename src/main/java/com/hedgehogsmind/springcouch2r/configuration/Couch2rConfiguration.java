package com.hedgehogsmind.springcouch2r.configuration;

public interface Couch2rConfiguration {

    /**
     * Shall return the base path under which the resources shall be made available.
     *
     * @return Base path of resources published.
     */
    String getCouch2rBasePath();

}

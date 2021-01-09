package com.hedgehogsmind.springcouchrest.configuration;

public class DummyTestCouchRestConfiguration
        extends CouchRestConfigurationAdapter {

    @Override
    public String getCouchRestBasePath() {
        return "/testing/api/";
    }

}

package com.hedgehogsmind.springcouchrest.demo;

import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfigurationAdapter;
import org.springframework.stereotype.Component;

@Component
public class MyCouchRestConfiguration
        extends CouchRestConfigurationAdapter {

    @Override
    public String getBaseSecurityRule() {
        return "permitAll()";
    }

    @Override
    public String getDefaultEndpointSecurityRule() {
        return "permitAll()";
    }
}

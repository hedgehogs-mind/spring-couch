package com.hedgehogsmind.springcouch2r.demo;

import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import org.springframework.stereotype.Component;

@Component
public class MyCouch2rConfiguration implements Couch2rConfiguration {

    @Override
    public String getCouch2rBasePath() {
        return "/api/couch2r";
    }

}

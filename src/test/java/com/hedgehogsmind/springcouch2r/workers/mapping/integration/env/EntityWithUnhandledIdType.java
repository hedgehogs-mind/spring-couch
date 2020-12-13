package com.hedgehogsmind.springcouch2r.workers.mapping.integration.env;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Couch2r
public class EntityWithUnhandledIdType {

    @Id
    public Date id;

}

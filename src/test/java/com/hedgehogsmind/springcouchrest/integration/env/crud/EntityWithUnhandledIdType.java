package com.hedgehogsmind.springcouchrest.integration.env.crud;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@CouchRest
public class EntityWithUnhandledIdType {

    @Id
    public Date id;

}

package com.hedgehogsmind.springcouchrest.demo.entities;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.security.CrudSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@CouchRest
@CrudSecurity(read = "permitAll()", saveUpdate = "permitAll()", delete = "permitAll()")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {


    private long id;

    private String street;

    private String nr;

    private String zip;

    private String town;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(nullable = false)
    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    @Column(nullable = false)
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(nullable = false)
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}

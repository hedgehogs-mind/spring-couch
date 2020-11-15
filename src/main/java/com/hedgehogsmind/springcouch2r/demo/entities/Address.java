package com.hedgehogsmind.springcouch2r.demo.entities;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Couch2r
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String nr;

    @Column(nullable = false)
    private String zip;

    @Column(nullable = false)
    private String town;

}

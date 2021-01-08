package com.hedgehogsmind.springcouchrest.rest;

import org.springframework.http.ResponseEntity;

public interface ResponseEntityConvertible<T> {

    /**
     * Implementation shall wrap itself as body and set the status as well as
     * content type header of the ResponseEntity accordingly.
     *
     * @return ResponseEntity.
     */
    ResponseEntity<T> toResponseEntity();

}

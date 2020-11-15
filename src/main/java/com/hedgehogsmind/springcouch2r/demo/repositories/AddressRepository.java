package com.hedgehogsmind.springcouch2r.demo.repositories;

import com.hedgehogsmind.springcouch2r.demo.entities.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

}

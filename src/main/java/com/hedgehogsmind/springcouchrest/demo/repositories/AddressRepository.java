package com.hedgehogsmind.springcouchrest.demo.repositories;

import com.hedgehogsmind.springcouchrest.demo.entities.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

}

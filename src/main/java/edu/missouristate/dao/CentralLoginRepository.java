package edu.missouristate.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.missouristate.domain.CentralLogin;

@Repository
public interface CentralLoginRepository extends CrudRepository<CentralLogin, Integer>{

}

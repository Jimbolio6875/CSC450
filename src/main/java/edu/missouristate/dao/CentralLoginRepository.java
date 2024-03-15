package edu.missouristate.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.missouristate.domain.CentralLogin;

@Repository
public interface CentralLoginRepository extends CrudRepository<CentralLogin, Integer>{

	public List<CentralLogin> findUsername(String username);
	public boolean authenticate(String username, String password);
}

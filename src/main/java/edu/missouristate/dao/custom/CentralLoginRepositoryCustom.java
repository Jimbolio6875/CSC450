package edu.missouristate.dao.custom;

import java.util.List;

import edu.missouristate.domain.CentralLogin;

public interface CentralLoginRepositoryCustom {

	public List<CentralLogin> findUsername(String username);
	public boolean authenticate(String username, String password);
}

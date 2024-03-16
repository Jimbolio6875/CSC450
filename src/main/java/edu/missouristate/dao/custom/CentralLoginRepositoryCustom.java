package edu.missouristate.dao.custom;

import java.util.List;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;

public interface CentralLoginRepositoryCustom {

	public List<CentralLogin> findUsername(String username);
	public LoginResponse authenticate(String username, String password);
}

package edu.missouristate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;

@Service
public class CentralLoginService {

	@Autowired
	CentralLoginRepository loginRepo;
	
	@Transactional
	// edit this to return success value, I'm just throwing this together rn
	// also edit this to hash password and then save
	public CentralLogin saveCentralLogin(CentralLogin centralLogin) {
		CentralLogin savedLogin = loginRepo.save(centralLogin);
		
		return savedLogin;
	}
	
	public boolean usernameExists(String username){
		List<CentralLogin> users = loginRepo.findUsername(username);
		if (users.size() >= 1) {
			return true;
		}
		return false;
	}
	
	public boolean register(CentralLogin login) {
		if (usernameExists(login.getUsername())) {
			return false;
		}
		else {
			CentralLogin loginResult = saveCentralLogin(login);
			return loginResult != null;
		}
	}
	
	public LoginResponse login(CentralLogin login) {
		LoginResponse response = new LoginResponse();
		if (loginRepo.authenticate(login.getUsername(), login.getPassword())){
			response.setLoggedIn(true);
			response.setUsername(login.getUsername());
		}
		else {
			response.setLoggedIn(false);
			response.setErrorMessage("Failed to authenticate");
			response.setErrorType("danger");
			response.setUsername("");
		}
		return response;
	}
}

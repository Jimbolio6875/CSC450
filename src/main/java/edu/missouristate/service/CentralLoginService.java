package edu.missouristate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;

@Service
public class CentralLoginService {

	@Autowired
	CentralLoginRepository loginRepo;
	
	@Transactional
	// edit this to return success value, I'm just throwing this together rn
	public CentralLogin saveCentralLogin(CentralLogin centralLogin) {
		CentralLogin savedLogin = loginRepo.save(centralLogin);
		
		return savedLogin;
	}
}

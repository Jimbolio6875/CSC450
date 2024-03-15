package edu.missouristate.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import edu.missouristate.dao.custom.CentralLoginRepositoryCustom;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.QCentralLogin;

public class CentralLoginRepositoryImpl extends QuerydslRepositorySupport implements CentralLoginRepositoryCustom{

	QCentralLogin centralLoginTable = QCentralLogin.centralLogin;
	
	public CentralLoginRepositoryImpl() {
		super(CentralLogin.class);
	}

	public List<CentralLogin> findUsername(String username) {
		
		return from(centralLoginTable)
				.where(centralLoginTable.username.eq(username))
				.fetch();
	}
	
	//TODO: use password hashing, account  for variable results
	public boolean authenticate(String username, String password) {
		List<CentralLogin> results = new ArrayList<CentralLogin>();
		results = from(centralLoginTable)
				.where(centralLoginTable.username.eq(username).and(centralLoginTable.password.eq(password)))
				.fetch();
		if (results.size() != 1) {
			return false;
		}
		else {
			return true;
		}
				
	}
}

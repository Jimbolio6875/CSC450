package edu.missouristate.dao.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import edu.missouristate.dao.custom.CentralLoginRepositoryCustom;
import edu.missouristate.domain.CentralLogin;

public class CentralLoginRepositoryImpl extends QuerydslRepositorySupport implements CentralLoginRepositoryCustom{

	public CentralLoginRepositoryImpl() {
		super(CentralLogin.class);
	}

}

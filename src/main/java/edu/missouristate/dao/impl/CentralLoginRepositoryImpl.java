package edu.missouristate.dao.impl;

import edu.missouristate.dao.custom.CentralLoginRepositoryCustom;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.QCentralLogin;
import edu.missouristate.dto.LoginResponse;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CentralLoginRepositoryImpl extends QuerydslRepositorySupport implements CentralLoginRepositoryCustom {

    QCentralLogin centralLoginTable = QCentralLogin.centralLogin;

    public CentralLoginRepositoryImpl() {
        super(CentralLogin.class);
    }

    public List<CentralLogin> findUsername(String username) {

        return from(centralLoginTable)
                .where(centralLoginTable.username.eq(username))
                .fetch();
    }

    public LoginResponse authenticate(String username, String password) {
        List<CentralLogin> results = new ArrayList<CentralLogin>();
        LoginResponse response = new LoginResponse();
       
        //retrieve result by user and hashed password (which is checked in the service method against provided plaintext)
        results = from(centralLoginTable)
                .where(centralLoginTable.username.eq(username).and(centralLoginTable.password.eq(password)))
                .fetch();
        if (results.size() != 1) {
        	//valid, guaranteed correct user not found if results not 1
            response.setLoggedIn(false);
            response.setMessage("Username not found.");
            response.setMessageType("danger");
            return response;
        } else {
        	//return single user if found
            CentralLogin login = results.get(0);
            response.setLoggedIn(true);
            response.setFirstName(login.getFirstName());
            response.setLastName(login.getLastName());
            response.setUsername(login.getUsername());
            response.setUserId(login.getCentralLoginId());
            return response;
        }
    }
    
    public String getHashedPasswordByUsername(String username) {
    	CentralLogin user = from(centralLoginTable)
    			.where(centralLoginTable.username.eq(username))
    			.fetchOne();
    	
    	//if user is null, their password is too
    	if (user == null){
    		return null;
    	}
    	return user.getPassword();
    }
}

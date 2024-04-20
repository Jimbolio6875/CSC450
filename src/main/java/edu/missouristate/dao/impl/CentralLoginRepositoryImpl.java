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

    //TODO: use password hashing, account  for variable results
    public LoginResponse authenticate(String username, String password) {
        List<CentralLogin> results = new ArrayList<CentralLogin>();
        LoginResponse response = new LoginResponse();
        results = from(centralLoginTable)
                .where(centralLoginTable.username.eq(username).and(centralLoginTable.password.eq(password)))
                .fetch();
        if (results.size() != 1) {
            response.setLoggedIn(false);
            response.setMessage("Failed to authenticate");
            response.setMessageType("danger");
            return response;
        } else {
            CentralLogin login = results.get(0);

            response.setLoggedIn(true);
            response.setFirstName(login.getFirstName());
            response.setLastName(login.getLastName());
            response.setUsername(login.getUsername());
            response.setUserId(login.getCentralLoginId());
            return response;
        }


    }
}

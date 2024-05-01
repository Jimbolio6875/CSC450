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

    /**
     * Retrieves all CentralLogin entries with the specified username
     *
     * @param username The username to search for
     * @return List of CentralLogin entities that match the given username
     */
    public List<CentralLogin> findUsername(String username) {

        return from(centralLoginTable)
                .where(centralLoginTable.username.eq(username))
                .fetch();
    }

    /**
     * Authenticates a user based on the provided username and password.
     * It checks for a single matching entry to ensure the correct user is found
     *
     * @param username The username of the user trying to log in
     * @param password The password provided by the user
     * @return LoginResponse containing the authentication result and user details if successful
     */
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

    /**
     * Retrieves the hashed password for a user based on their username.
     * Returns null if the username does not exist in the database
     *
     * @param username The username whose password hash is to be retrieved
     * @return The hashed password or null if the username is not found
     */
    public String getHashedPasswordByUsername(String username) {
        CentralLogin user = from(centralLoginTable)
                .where(centralLoginTable.username.eq(username))
                .fetchOne();

        //if user is null, their password is too
        if (user == null) {
            return null;
        }
        return user.getPassword();
    }
}

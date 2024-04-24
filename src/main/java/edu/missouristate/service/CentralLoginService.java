package edu.missouristate.service;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CentralLoginService {

    @Autowired
    CentralLoginRepository loginRepo;

    Argon2PasswordEncoder pwEncoder = new Argon2PasswordEncoder(32,64,2,15*1024,2);
    
    @Transactional
    // edit this to return success value, I'm just throwing this together rn
    // also edit this to hash password and then save
    public CentralLogin saveCentralLogin(CentralLogin centralLogin) {
    	//hash password
    	String encodedPassword = pwEncoder.encode(centralLogin.getPassword());
    	System.out.println("Password length" + encodedPassword.length());
    	System.out.println("Password hash: " + encodedPassword);
    	System.out.println("Raw and hashed password match: " + pwEncoder.matches(centralLogin.getPassword(), encodedPassword));
    	centralLogin.setPassword(encodedPassword);
    	
        CentralLogin savedLogin = loginRepo.save(centralLogin);
        
        return savedLogin;
    }

    public boolean usernameExists(String username) {
        List<CentralLogin> users = loginRepo.findUsername(username);
        if (users.size() >= 1) {
            return true;
        }
        return false;
    }

    //TODO: validate password server side
    @Transactional
    public boolean register(CentralLogin login) {
        if (usernameExists(login.getUsername())) {
            return false;
        } else {
        	
            CentralLogin loginResult = saveCentralLogin(login);
            return loginResult != null;
        }
    }

    public LoginResponse login(CentralLogin login) {
    	LoginResponse response;
    	String hashedPassword = loginRepo.getHashedPasswordByUsername(login.getUsername());
    	
    	if (hashedPassword == null) {
    		response = new LoginResponse();
    		response.setLoggedIn(false);
            response.setMessage("User not found");
            response.setMessageType("danger");
            return response;
    	} else if (pwEncoder.matches(login.getPassword(), hashedPassword)) {
    		response = loginRepo.authenticate(login.getUsername(), hashedPassword);
            return response;
    	} else {
    		response = new LoginResponse();
    		response.setLoggedIn(false);
            response.setMessage("Incorrect password provided");
            response.setMessageType("danger");
            return response;
    	}
    }

    public CentralLogin getUserById(Integer userId) {
        return loginRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

}

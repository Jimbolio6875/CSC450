package edu.missouristate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.GenericResponse;
import edu.missouristate.dto.LoginResponse;

@Service
public class CentralLoginService {

    @Autowired
    CentralLoginRepository loginRepo;

    Argon2PasswordEncoder pwEncoder = new Argon2PasswordEncoder(32,64,2,15*1024,2);
    
    // Saves a central login after hashing provided password
    @Transactional
    public CentralLogin saveCentralLogin(CentralLogin centralLogin) {
    	//hash password
    	String encodedPassword = pwEncoder.encode(centralLogin.getPassword());
    	centralLogin.setPassword(encodedPassword);
    	
        CentralLogin savedLogin = loginRepo.save(centralLogin);
        
        return savedLogin;
    }

    // Checks if username already exists in DB
    public boolean usernameExists(String username) {
        List<CentralLogin> users = loginRepo.findUsername(username);
        if (users.size() >= 1) {
            return true;
        }
        return false;
    }

    // Creates new user from provided credentials from "register" page
    @Transactional
    public GenericResponse register(CentralLogin login) {
    	GenericResponse response = new GenericResponse();
    	
    	//store in local variable so we can calculate once and use in conditional for response message
    	boolean usernameExists = usernameExists(login.getUsername());
        if (usernameExists || login.getUsername().length() == 0 || login.getPassword().length() == 0) {
        	//invalid username/pw provided, send fail response
        	response.setMessage(usernameExists ? "Username already exists." : "Registration failed.");
            response.setMessageType("danger");
            return response;
        } else if(!login.getPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
        	response.setMessage("Invalid Password");
            response.setMessageType("danger");
            return response;
        } else {
        	//valid username/pw provided, now try to save and return response
            CentralLogin loginResult = saveCentralLogin(login);
            if (loginResult == null) {
            	//save failed, send fail response
            	response.setMessage("Registration failed.");
                response.setMessageType("danger");
                return response;
            }
            //result not null, send success
            response.setMessage("Registration success");
            response.setMessageType("success");
            return response;
        }
    }
    
    // Compares provided credentials to the DB and authenticates/denies user based on result.
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

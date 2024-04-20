package edu.missouristate.service;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        LoginResponse response = loginRepo.authenticate(login.getUsername(), login.getPassword());
        return response;
    }

    public CentralLogin getUserById(Integer userId) {
        return loginRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

}

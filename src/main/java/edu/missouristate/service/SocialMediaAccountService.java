package edu.missouristate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.missouristate.dao.SocialMediaAccountRepository;
import edu.missouristate.domain.SocialMediaAccount;

@Service
public class SocialMediaAccountService {

    @Autowired
    private SocialMediaAccountRepository repository;

    public SocialMediaAccount saveSocialMediaAccount(Integer userId, String platformName, String accessToken) {
        SocialMediaAccount account = new SocialMediaAccount();
        account.setUserId(userId);
        account.setPlatformName(platformName);
        account.setAccessToken(accessToken);
        return repository.save(account);
    }
}

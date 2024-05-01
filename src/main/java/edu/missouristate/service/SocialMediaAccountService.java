package edu.missouristate.service;


import edu.missouristate.dao.SocialMediaAccountRepository;
import edu.missouristate.domain.SocialMediaAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialMediaAccountService {

    @Autowired
    private SocialMediaAccountRepository repository;

    /**
     * Saves a new social media account to the repository.
     * Creates a new SocialMediaAccount object, sets its properties from the provided parameters,
     * and saves it using the repository
     *
     * @param userId       The user ID associated with the social media account
     * @param platformName The name of the social media platform
     * @param accessToken  The access token for the social media platform
     * @return The saved SocialMediaAccount object
     */
    public SocialMediaAccount saveSocialMediaAccount(Integer userId, String platformName, String accessToken) {
        SocialMediaAccount account = new SocialMediaAccount();
        account.setUserId(userId);
        account.setPlatformName(platformName);
        account.setAccessToken(accessToken);
        return repository.save(account);
    }
}

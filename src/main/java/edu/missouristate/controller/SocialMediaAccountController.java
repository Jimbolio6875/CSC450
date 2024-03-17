package edu.missouristate.controller;


import edu.missouristate.domain.SocialMediaAccount;
import edu.missouristate.service.SocialMediaAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialMediaAccountController {

    private final SocialMediaAccountService service;

    @Autowired
    public SocialMediaAccountController(SocialMediaAccountService service) {
        this.service = service;
    }

    @PostMapping("/socialMediaAccounts")
    public ResponseEntity<SocialMediaAccount> addSocialMediaAccount(@RequestBody SocialMediaAccount account) {
        try {
            SocialMediaAccount savedAccount = service.saveSocialMediaAccount(account.getUserId(), account.getPlatformName(), account.getAccessToken());
            return ResponseEntity.ok(savedAccount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

package edu.missouristate.domain;

import javax.persistence.*;

@Entity
@Table(name = "social_media_accounts")
public class SocialMediaAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    private Integer userId;
    private String platformName;
    private String accessToken;

    public SocialMediaAccount() {
    }

    public SocialMediaAccount(Integer userId, String platformName, String accessToken) {
        this.userId = userId;
        this.platformName = platformName;
        this.accessToken = accessToken;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

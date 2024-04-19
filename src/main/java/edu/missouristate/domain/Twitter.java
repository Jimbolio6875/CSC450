package edu.missouristate.domain;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "twitter")
public class Twitter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Integer id;

    @Column(name = "tweet_text", columnDefinition = "TEXT")
    private String tweetText;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @Column(name = "access_token", columnDefinition = "VARCHAR(320)")
    private String accessToken;

    @Column(name = "access_token_secret", columnDefinition = "VARCHAR(320)")
    private String accessTokenSecret;

    @ManyToOne
    @JoinColumn(name = "central_login_id", referencedColumnName = "central_login_id")
    private CentralLogin centralLogin;

    public Twitter() {
    }

    public CentralLogin getCentralLogin() {
        return centralLogin;
    }

    public void setCentralLogin(CentralLogin centralLogin) {
        this.centralLogin = centralLogin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }
}

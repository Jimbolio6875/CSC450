package edu.missouristate.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "central_login")
public class CentralLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "central_login_id", columnDefinition = "Integer")
    private Integer centralLoginId;
    @Column(name = "username", columnDefinition = "VARCHAR(32)")
    private String username;
    @Column(name = "password", columnDefinition = "VARCHAR(32)")
    private String password;
    @Column(name = "first_name", columnDefinition = "VARCHAR(32)")
    private String firstName;
    @Column(name = "last_name", columnDefinition = "VARCHAR(32)")
    private String lastName;
    // One-to-many relationship to Mastodon
    @OneToMany(mappedBy = "centralLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Mastodon> mastodonPosts;

    // One-to-many relationship to Reddit
    @OneToMany(mappedBy = "centralLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RedditPosts> redditPosts;

    // One-to-many relationship to Tumblr
    @OneToMany(mappedBy = "centralLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tumblr> tumblrPosts;

    // One-to-many relationship to Twitter
    @OneToMany(mappedBy = "centralLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Twitter> twitterPosts;

    public CentralLogin() {

    }

    public Set<Mastodon> getMastodonPosts() {
        return mastodonPosts;
    }

    public void setMastodonPosts(Set<Mastodon> mastodonPosts) {
        this.mastodonPosts = mastodonPosts;
    }

    public Set<RedditPosts> getRedditPosts() {
        return redditPosts;
    }

    public void setRedditPosts(Set<RedditPosts> redditPosts) {
        this.redditPosts = redditPosts;
    }

    public Set<Tumblr> getTumblrPosts() {
        return tumblrPosts;
    }

    public void setTumblrPosts(Set<Tumblr> tumblrPosts) {
        this.tumblrPosts = tumblrPosts;
    }

    public Set<Twitter> getTwitterPosts() {
        return twitterPosts;
    }

    public void setTwitterPosts(Set<Twitter> twitterPosts) {
        this.twitterPosts = twitterPosts;
    }

    @Override
    public String toString() {
        return "CentralLogin [centralLoginId=" + centralLoginId + ", username=" + username + ", password=" + password
                + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getCentralLoginId() {
        return centralLoginId;
    }

    public void setCentralLoginId(Integer centralLoginId) {
        this.centralLoginId = centralLoginId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
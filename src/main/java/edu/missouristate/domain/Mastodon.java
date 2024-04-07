package edu.missouristate.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mastodon")
public class Mastodon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id", columnDefinition = "VARCHAR(255)")
    private String postId;

    @Column(name = "access_token", columnDefinition = "VARCHAR(1000)", nullable = false)
    private String accessToken;
    @Column(name = "user_id", columnDefinition = "VARCHAR(255)")
    private String userId;
    @Column(name = "content", columnDefinition = "VARCHAR(255)")
    private String content;
    @Column(name = "post_url", columnDefinition = "VARCHAR(255)")
    private String postUrl;
    @Column(name = "favourite_count", columnDefinition = "INTEGER")
    private Integer favouriteCount;

    public Mastodon() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public Integer getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(Integer favouriteCount) {
        this.favouriteCount = favouriteCount;
    }
}

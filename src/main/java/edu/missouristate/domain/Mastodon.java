package edu.missouristate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "mastodon")
public class Mastodon implements Serializable {

    public Mastodon() {};

    @Id
    @Column(name = "post_id", columnDefinition = "VARCHAR(255)")
    private String postId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(255)")
    private String userId;

    @Column(name = "content", columnDefinition = "VARCHAR(255)")
    private String content;

    @Column(name = "post_url", columnDefinition = "VARCHAR(255)")
    private String postUrl;

    @Column(name = "favourite_count", columnDefinition = "INTEGER")
    private Integer favouriteCount;

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

    @Override
    public String toString() {
        return "Mastodon{" +
                "postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", favouriteCount=" + favouriteCount +
                '}';
    }
}

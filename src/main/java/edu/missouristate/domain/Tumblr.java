package edu.missouristate.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
//id              SERIAL PRIMARY KEY,
//access_token    VARCHAR(255) NOT NULL,
//token_secret    VARCHAR(255) NOT NULL,
//blog_identifier VARCHAR(255) NOT NULL,

@Entity
@Table(name = "tumblr")
public class Tumblr implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Integer id;

    @Column(name = "post_id", columnDefinition = "VARCHAR(255)")
    private String postId;

    @Column(name = "access_token", columnDefinition = "VARCHAR(255)", nullable = false)
    private String accessToken;

    @Column(name = "token_secret", columnDefinition = "VARCHAR(255)", nullable = false)
    private String tokenSecret;

    @Column(name = "blog_identifier", columnDefinition = "VARCHAR(255)", nullable = false)
    private String blogIdentifier;

    @Column(name = "content", columnDefinition = "VARCHAR(255)")
    private String content;
    @Column(name = "post_url", columnDefinition = "VARCHAR(255)")
    private String postUrl;
    @Column(name = "note_count", columnDefinition = "INTEGER")
    private Integer noteCount;
    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private Timestamp date;

    public Tumblr() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getBlogIdentifier() {
        return blogIdentifier;
    }

    public void setBlogIdentifier(String blogIdentifier) {
        this.blogIdentifier = blogIdentifier;
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

    public Integer getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(Integer noteCount) {
        this.noteCount = noteCount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}

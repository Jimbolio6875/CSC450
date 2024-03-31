package edu.missouristate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tumblr")
public class Tumblr implements Serializable {

    public Tumblr() {};

    @Id
    @Column(name = "post_id", columnDefinition = "VARCHAR(255)")
    private String postId;

    @Column(name = "blog_identifier", columnDefinition = "VARCHAR(255)")
    private String blogIdentifier;

    @Column(name = "content", columnDefinition = "VARCHAR(255)")
    private String content;

    @Column(name = "post_url", columnDefinition = "VARCHAR(255)")
    private String postUrl;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    @Override
    public String toString() {
        return "Tumblr{" +
                "postId='" + postId + '\'' +
                ", blogIdentifier='" + blogIdentifier + '\'' +
                ", content='" + content + '\'' +
                ", postUrl='" + postUrl + '\'' +
                '}';
    }
}

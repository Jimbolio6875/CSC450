package edu.missouristate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

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

    @Column(name = "note_count", columnDefinition = "INTEGER")
    private Integer noteCount;

    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private Timestamp date;

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

    @Override
    public String toString() {
        return "Tumblr{" +
                "postId='" + postId + '\'' +
                ", blogIdentifier='" + blogIdentifier + '\'' +
                ", content='" + content + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", noteCount=" + noteCount +
                ", date=" + date +
                '}';
    }
}

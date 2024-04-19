package edu.missouristate.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "reddit_posts")
public class RedditPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Integer id;
    @Column(name = "post_id", columnDefinition = "VARCHAR(255)")
    private String postId;
    @Column(name = "subreddit", columnDefinition = "VARCHAR(255)")
    private String subreddit;
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "author", columnDefinition = "VARCHAR(225)")
    private String author;

    @Column(name = "up_votes", columnDefinition = "INTEGER")
    private Integer upVotes = 0;

    @Column(name = "down_votes", columnDefinition = "INTEGER")
    private Integer downVotes = 0;

    @Column(name = "score", columnDefinition = "INTEGER")
    private Integer score = 0;

    @Column(name = "num_comments", columnDefinition = "INTEGER")
    private Integer numComments = 0;

    @Column(name = "access_token", columnDefinition = "VARCHAR(1000)", nullable = false)
    private String accessToken;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "central_login_id", referencedColumnName = "central_login_id")
    private CentralLogin centralLogin;

    public RedditPosts() {
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(Integer upVotes) {
        this.upVotes = upVotes;
    }

    public Integer getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(Integer downVotes) {
        this.downVotes = downVotes;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getNumComments() {
        return numComments;
    }

    public void setNumComments(Integer numComments) {
        this.numComments = numComments;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}


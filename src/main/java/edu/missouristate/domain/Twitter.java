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

    @Column(name = "tweet_id", columnDefinition = "BIGINT", nullable = false)
    private Long tweetId;

    @Column(name = "tweet_text", columnDefinition = "TEXT", nullable = false)
    private String tweetText;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate = LocalDateTime.now();

    public Twitter() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
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

    @Override
    public String toString() {
        return "Twitter{" +
                "id=" + id +
                ", tweetId=" + tweetId +
                ", tweetText='" + tweetText + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}

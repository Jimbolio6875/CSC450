package edu.missouristate.service;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Twitter;

import java.time.LocalDateTime;
import java.util.List;

public interface TwitterService {

    public Twitter saveTweet(Twitter tweet);

//    List<Twitter> findByTweetId(Long userId);

    List<Twitter> getAllTweets();


    boolean postTweet(String message, String accessToken, String accessTokenSecret);

    Tuple getLatestUser();

    void updateContent(String accessToken, String message, LocalDateTime date, String accessTokenSecret, Integer userId);

    List<Twitter> getAllTweetsWhereCreationIsNotNullAndSameUserid(Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);
}

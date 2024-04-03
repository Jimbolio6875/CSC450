package edu.missouristate.service;

import edu.missouristate.domain.Twitter;

import java.util.List;

public interface TwitterService {

    public Twitter saveTweet(Twitter tweet);

    List<Twitter> findByTweetId(Long userId);

    List<Twitter> getAllTweets();


}

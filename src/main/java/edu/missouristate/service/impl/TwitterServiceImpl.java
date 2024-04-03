package edu.missouristate.service.impl;

import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.TwitterRepository;
import edu.missouristate.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TwitterServiceImpl implements TwitterService {

    private static final Logger log = LoggerFactory.getLogger(TwitterServiceImpl.class);

    @Autowired
    TwitterRepository twitterRepository;

    @Override
    public Twitter saveTweet(Twitter tweet) {
        return twitterRepository.save(tweet);
    }

    @Override
    public List<Twitter> findByTweetId(Long userId) {
        return twitterRepository.findByTweetId(userId);
    }

    @Override
    public List<Twitter> getAllTweets() {
        return (List<Twitter>) twitterRepository.findAll();
    }


}

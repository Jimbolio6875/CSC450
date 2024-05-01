package edu.missouristate.service.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.TwitterRepository;
import edu.missouristate.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TwitterServiceImpl implements TwitterService {

    private static final Logger log = LoggerFactory.getLogger(TwitterServiceImpl.class);
    @Autowired
    CentralLoginRepository centralLoginRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    TwitterRepository twitterRepository;
    @Value("${python.path}")
    private String pythonPath;

    /**
     * Saves a Twitter post entity to the database
     *
     * @param tweet The Twitter entity to save
     * @return The saved Twitter entity
     */
    @Override
    public Twitter saveTweet(Twitter tweet) {
        return twitterRepository.save(tweet);
    }

    /**
     * Retrieves all Twitter posts from the database
     *
     * @return A list of all Twitter entities
     */
    @Override
    public List<Twitter> getAllTweets() {
        return (List<Twitter>) twitterRepository.findAll();
    }

    /**
     * Posts a tweet via a Python script using the provided access token and secret
     *
     * @param tweetText         The text of the tweet
     * @param accessToken       The access token for Twitter API
     * @param accessTokenSecret The secret token for Twitter API
     * @return True if the post is successful, false otherwise
     */
    @Override
    public boolean postTweet(String tweetText, String accessToken, String accessTokenSecret) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonPath,
                    "scripts/TwitterPythonScripts/twitter_post_manager.py",
                    accessToken,
                    accessTokenSecret,
                    tweetText
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.debug("Tweet posted successfully. Script output: {}", scriptOutput);
                return true; // or handle the output more specifically if needed
            } else {
                log.error("Failed to post tweet, exit code: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("Exception occurred while posting tweet: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Retrieves the latest Twitter user credentials from the repository
     *
     * @return A tuple containing the latest access token and secret
     */
    @Override
    public Tuple getLatestUser() {
        return twitterRepository.getLatestUser();
    }

    /**
     * Updates or creates a Twitter post with the given content and associates it with a user
     *
     * @param accessToken       The access token for the Twitter API
     * @param message           The tweet text
     * @param localDateTime     The creation date of the tweet
     * @param accessTokenSecret The secret token for Twitter API
     * @param userId            The user ID to associate with the tweet
     */
    @Override
    public void updateContent(String accessToken, String message, LocalDateTime localDateTime, String accessTokenSecret, Integer userId) {
        // Find an existing Twitter post with the same accessToken but no associated tweet text
        Twitter existingPost = twitterRepository.findExistingPostByTokenAndNoText(accessToken);


        if (existingPost != null) {
            // If a post exists, update the message, the creation date, and potentially the user
            existingPost.setTweetText(message);
            existingPost.setCreationDate(localDateTime);
            CentralLogin user = entityManager.getReference(CentralLogin.class, userId);
            existingPost.setCentralLogin(user);
            twitterRepository.save(existingPost);
        } else {
            // If no post exists, create a new one and associate it with a user
            Twitter newTwitterPost = new Twitter();
            newTwitterPost.setAccessToken(accessToken);
            newTwitterPost.setAccessTokenSecret(accessTokenSecret);
            newTwitterPost.setTweetText(message);
            newTwitterPost.setCreationDate(localDateTime);
            CentralLogin user = entityManager.getReference(CentralLogin.class, userId);
            newTwitterPost.setCentralLogin(user);

            // Save the new Twitter post
            twitterRepository.save(newTwitterPost);
        }
    }

    /**
     * Retrieves all Twitter posts for a specific user that have a non-null creation date
     *
     * @param userId The user ID to filter tweets
     * @return List of Twitter entities
     */
    @Override
    public List<Twitter> getAllTweetsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return twitterRepository.getAllTweetsWhereCreationIsNotNullAndSameUserid(userId);
    }

    /**
     * Cleans up entries in the Twitter table where the creation date is null for a specific user
     *
     * @param userId The user ID associated with the cleanup operation
     */
    @Override
    public void cleanTable(Integer userId) {
        twitterRepository.cleanTable(userId);
    }

    /**
     * Checks if there are any stored access tokens for a specific user in the Twitter table
     *
     * @param userId The user ID to check
     * @return true if at least one token exists, otherwise false
     */
    @Override
    public boolean hasToken(Integer userId) {
        return twitterRepository.hasToken(userId);
    }


}

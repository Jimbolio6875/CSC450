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

    @Override
    public Twitter saveTweet(Twitter tweet) {
        return twitterRepository.save(tweet);
    }

//    @Override
//    public List<Twitter> findByTweetId(Long userId) {
//        return twitterRepository.findByTweetId(userId);
//    }

    @Override
    public List<Twitter> getAllTweets() {
        return (List<Twitter>) twitterRepository.findAll();
    }

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

    @Override
    public Tuple getLatestUser() {
        return twitterRepository.getLatestUser();
    }

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


    @Override
    public List<Twitter> getAllTweetsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return twitterRepository.getAllTweetsWhereCreationIsNotNullAndSameUserid(userId);
    }

    @Override
    public void cleanTable(Integer userId) {
        twitterRepository.cleanTable(userId);
    }

    @Override
    public boolean hasToken(Integer userId) {
        return twitterRepository.hasToken(userId);
    }


}

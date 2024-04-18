package edu.missouristate.service.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.TwitterRepository;
import edu.missouristate.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateContent(String accessToken, String message, LocalDateTime localDateTime, String accessTokenSecret) {

        Twitter exists = twitterRepository.findExistingPostByTokenAndNoText(accessToken);

        if (exists != null) {
            exists.setTweetText(message);
            exists.setCreationDate(localDateTime);
            twitterRepository.save(exists);
        } else {
            Twitter twitter = new Twitter();
            twitter.setAccessToken(accessToken);
            twitter.setAccessTokenSecret(accessTokenSecret);
            twitter.setTweetText(message);
            twitter.setCreationDate(localDateTime);
            twitterRepository.save(twitter);
        }

    }

    @Override
    public List<Twitter> getAllTweetsWhereCreationIsNotNull() {
        return twitterRepository.getAllTweetsWhereCreationIsNotNull();
    }

    @Override
    public void cleanTable() {
        twitterRepository.cleanTable();
    }

    @Override
    public boolean hasToken() {
        return twitterRepository.hasToken();
    }


}

package edu.missouristate.controller;

import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.domain.Twitter;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.RedditPostsService;
import edu.missouristate.service.TumblrService;
import edu.missouristate.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class PostHistoryController {

    @Autowired
    MastodonService mastodonService;

    @Autowired
    TumblrService tumblrService;

    @Autowired
    RedditPostsService redditPostsService;

    @Autowired
    TwitterService twitterService;

    @GetMapping("/postHistory")
    public ModelAndView getPostHistory() throws IOException, ExecutionException, InterruptedException {
        ModelAndView modelAndView = new ModelAndView("postHistory");


        List<Tumblr> tumblrPosts = new ArrayList<>();

        List<String> redditPostIds = redditPostsService.getAllRedditPostIds();
        List<RedditPosts> redditPosts = redditPostsService.fetchRedditPostDetails(redditPostIds);

//        List<String> mastrodonPostIds = mastodonService.getPostsByUserId();
//        List<Mastodon> mastodonListIds = mastodonService.fetchMastodonPostsByUserId()

//        tumblrService.updatePosts();
//        List<String> tumblrIds = tumblrService.getAllTubmlrIds();
//        tumblrPosts = tumblrService.tumblrPosts(tumblrIds); // Fetch Tumblr posts


        List<Twitter> tweets = twitterService.getAllTweets();


        modelAndView.addObject("redditPosts", redditPosts);
        modelAndView.addObject("tweets", tweets);
//        modelAndView.addObject("posts", tumblrPosts);

        return modelAndView;
    }
}


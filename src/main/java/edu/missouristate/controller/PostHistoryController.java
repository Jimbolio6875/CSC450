package edu.missouristate.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.domain.Twitter;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.RedditPostsService;
import edu.missouristate.service.TumblrService;
import edu.missouristate.service.TwitterService;

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
    public ModelAndView getPostHistory(HttpSession session) throws IOException, ExecutionException, InterruptedException {
        ModelAndView modelAndView = new ModelAndView("postHistory");
        Integer centralLoginId = (Integer) session.getAttribute("centralLoginId");

        List<String> redditPostIds = redditPostsService.getAllRedditPostIds();
        List<RedditPosts> redditPosts = redditPostsService.fetchRedditPostDetails(redditPostIds);

//        List<String> mastrodonPostIds = mastodonService.getPostsByUserId();
//        List<Mastodon> mastodonListIds = mastodonService.fetchMastodonPostsByUserId()

//        tumblrService.updatePosts();
//        List<String> tumblrIds = tumblrService.getAllTubmlrIds();
//        tumblrPosts = tumblrService.tumblrPosts(tumblrIds); // Fetch Tumblr posts

        //List<Twitter> tweets = twitterService.getAllTweets();
        List<Twitter> tweets = twitterService.findCentralUserTweets(centralLoginId);
        

        // todo im like 99% sure the mastodon and tumblr post calls just get everything from the database no matter the current user
        // todo will check whenever i can sign out and make a new account
        // will have to implement user id as foreign key in mastodon/tumblr tables

        //List<Mastodon> mastodonPosts = mastodonService.getAllPosts();
        List<Mastodon> mastodonPosts = mastodonService.findCentralUserMastodons(centralLoginId);
//        Collections.reverse(mastodonPosts);

        //List<Tumblr> tumblrPosts = tumblrService.getAllPosts();
//        Collections.reverse(tumblrPosts);
        List<Tumblr> tumblrPosts = tumblrService.findCentralUserTumblrs(centralLoginId);

        modelAndView.addObject("redditPosts", redditPosts);
        modelAndView.addObject("tweets", tweets);
        modelAndView.addObject("mastodonPosts", mastodonPosts);
        modelAndView.addObject("tumblrPosts", tumblrPosts);
//        modelAndView.addObject("posts", tumblrPosts);

        return modelAndView;
    }
}

